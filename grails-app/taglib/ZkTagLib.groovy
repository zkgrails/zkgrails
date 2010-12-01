
import grails.util.Environment
import grails.util.GrailsUtil
import grails.util.Metadata
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.web.mapping.UrlCreator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import org.springframework.web.servlet.support.RequestContextUtils as RCU

import org.zkoss.zkgrails.ZulResponse
import org.zkoss.util.resource.Labels
import org.zkoss.util.Locales

class ZkTagLib implements ApplicationContextAware, InitializingBean {

    static namespace = "z"

    ApplicationContext applicationContext
    GrailsPluginManager pluginManager

    public void afterPropertiesSet() {
      def config = applicationContext.getBean(GrailsApplication.APPLICATION_ID).config
      if(config.grails.views.enable.jsessionid instanceof Boolean) {
         useJsessionId = config.grails.views.enable.jsessionid
      }
    }

    def head = { attrs, b ->
        cacheZul(attrs['zul'])
        out << "<head>\n"
        out << "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n"
        out << "<meta http-equiv=\"Expires\" content=\"-1\" />\n"
        out	<< b()
        out << pageScope.model['head']
        out << "</head>\n"
    }

    def body = { attrs, b ->
        cacheZul(attrs['zul'])
        out << "<body>\n"
        out << b()
        def autoHeight = attrs['autoHeight']
        if(autoHeight==null || (autoHeight!=null && autoHeight=="true"))
            out << pageScope.model['body']
        else
            out << pageScope.model['_body']
        out << "</body>\n"
    }

    def div = { attrs, b ->
        cacheZul(attrs['zul'])
        out << pageScope.model[attrs['part']]
    }

    def l = { attrs, b ->
        String key = attrs["key"] as String
        List args = attrs["args"] as List
        Boolean required = attrs["required"] as Boolean
        String defValue = attrs["default"] as String
        def label

        if(!key) {
            throwTagError("Attribute [key] is obrigatory")
        }
        if(required != null && !(required instanceof Boolean)) {
            throwTagError("Attribute [required] has to be a boolean value [true|false]")
        }
        if(args && !(args instanceof List)) {
            throwTagError("Attribute [args] has to be a list")
        }
        if(defValue && !(defValue instanceof String)) {
            throwTagError("Attribute [default] has to be a String")
        }

        if(! required) {
            if(! defValue) {
                if(! args) {
                    label = Labels.getLabel(key)
                } else {
                    label = Labels.getLabel(key, args as Object[])
                }
            } else {
                if(args) {
                    label = Labels.getLabel(key, defValue, args as Object[])
                } else {
                    label = Labels.getLabel(key, defValue)
                }
            }
        } else {
            if(! args) {
                label = Labels.getRequiredLabel(key)
            } else {
                label = Labels.getRequiredLabel(key, args as Object[])
            }
        }

        out << label
    }

    /**
     * Get the declared URL of the server from config, or guess at localhost for non-production
     */
    private String makeServerURL() {
        def u = ConfigurationHolder.config?.grails?.serverURL
        if (!u) {
            // Leave it null if we're in production so we can throw
            if (Environment.current != Environment.PRODUCTION) {
                u = "http://localhost:" +(System.getProperty('server.port') ? System.getProperty('server.port') : "8080")
            }
        }
        return u
    }

    /**
     * Check for "absolute" attribute and render server URL if available from Config or deducible in non-production
     */
    private handleAbsolute(attrs) {
        def base = attrs.remove('base')
        if(base) {
            return base
        }
        else {
            def abs = attrs.remove("absolute")
            if (Boolean.valueOf(abs)) {
                def u = makeServerURL()
                if (u) {
                    return u
                } else {
                    throwTagError("Attribute absolute='true' specified but no grails.serverURL set in Config")
                }
            }
            else {
                //
                // return nothing because ZK will handle contextPath automatically
                //
                return "" // GrailsWebRequest.lookup(request).contextPath
            }
        }
    }

    def resource = { attrs, body ->
        def result = resourceImpl(attrs)
        out << result
    }

    def resourceImpl(attrs) {
        def writer = new StringBuffer("")
        writer << handleAbsolute(attrs)
        def dir = attrs['dir']
        if(attrs.plugin) {
            def pp = pluginManager.getPluginPath(attrs.plugin)
            writer << (pp ?: '')
        }
        else {
            if(attrs.contextPath) {
                writer << attrs.contextPath.toString()
            }
            else {
                def pluginContextPath = pageScope.pluginContextPath
                if(dir != pluginContextPath) {
                    def pcp = pluginContextPath
                    writer << (pcp ?: '')
                }
            }
        }
        if(dir) {
           writer << (dir.startsWith("/") ?  dir : "/${dir}")
        }
        def file = attrs['file']
        if(file) {
           writer << (file.startsWith("/") || dir?.endsWith('/') ?  file : "/${file}")
        }
        return writer.toString()
    }

    private cacheZul(url) {
        if(pageScope.variables.containsKey("cached")==false) {
            if(url==null) {
                url = "/${controllerName}/${actionName}.zul"
            }
            def zres = new ZulResponse(url, request, response, servletContext)
            if(pageScope.variables.containsKey("model")==false) {
                if(zres.status.ok) {
                    pageScope.model  = zres.model
                    pageScope.cached = true
                } else {
                    throw zres.status.exception
                }
            }
        } else {
            // do nothing
        }
    }

    /**
     * Resolves a message code for a given error or code from the resource bundle
     */
    def message = { attrs ->
        out << messageImpl(attrs)
    }

    private messageImpl(attrs) {
        def messageSource = applicationContext.getBean("messageSource")
        def locale = attrs.locale ?: Locales.getCurrent()// RCU.getLocale(request)
        // println locale

        def text
        def error = attrs['error'] ?: attrs['message']
        if(error) {
            try {
                text = messageSource.getMessage( error , locale )
            } catch (NoSuchMessageException e) {
                if(error instanceof MessageSourceResolvable) {
                    text = error?.code
                } else {
                    text = error?.toString()
                }
            }
        } else if(attrs['code']) {
            def code = attrs['code']
            def args = attrs['args']
            def defaultMessage = ( attrs['default'] != null ? attrs['default'] : code )

            def message = messageSource.getMessage( code,
                                                    args == null ? null : args.toArray(),
                                                    defaultMessage,
                                                    locale )
            if(message) {
                text = message
            }
            else {
                text = defaultMessage
            }
        }
        if (text) {
            return text
            // return (attrs.encodeAs ? text."encodeAs${attrs.encodeAs}"() : text)
        }
        return ''
    }

}
