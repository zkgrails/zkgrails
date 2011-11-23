import grails.util.Environment

import org.codehaus.groovy.grails.commons.GrailsApplication

import org.codehaus.groovy.grails.plugins.GrailsPluginManager

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException

import org.zkoss.zk.grails.ZulResponse
import org.zkoss.util.resource.Labels
import org.zkoss.util.Locales

import org.zkoss.zk.fn.JspFns

class ZkTagLib implements ApplicationContextAware {

    static namespace = "z"

    GrailsApplication grailsApplication
    ApplicationContext applicationContext
    GrailsPluginManager pluginManager

    def wrapper = { attrs, b ->
        def url = attrs.remove('url')
        if (url == null) {
            url = "/${controllerName}/${actionName}.zul"
        }
        out << '''<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="-1" />
'''
        out << JspFns.outZkHtmlTags(servletContext, request, response, null)
        out << "\n"
        out << "</head>\n"
        out << "<body>\n"
        def zres = new ZulResponse(url, request, response, servletContext)
        out << zres.model['source']
        out << "</body>\n"
        out << "</html>"
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

        if (!key) {
            throwTagError("Attribute [key] is obrigatory")
        }
        if (required != null && !(required instanceof Boolean)) {
            throwTagError("Attribute [required] has to be a boolean value [true|false]")
        }
        if (args && !(args instanceof List)) {
            throwTagError("Attribute [args] has to be a list")
        }
        if (defValue && !(defValue instanceof String)) {
            throwTagError("Attribute [default] has to be a String")
        }

        if (!required) {
            if (!defValue) {
                if (!args) {
                    label = Labels.getLabel(key)
                } else {
                    label = Labels.getLabel(key, args as Object[])
                }
            } else {
                if (args) {
                    label = Labels.getLabel(key, defValue, args as Object[])
                } else {
                    label = Labels.getLabel(key, defValue)
                }
            }
        } else {
            if (!args) {
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
                u = "http://localhost:" + (System.getProperty('server.port') ? System.getProperty('server.port') : "8080")
            }
        }
        return u
    }

    def resource = { attrs ->
        def r = applicationContext.getBean("org.grails.plugin.resource.ResourceTagLib")
        def result = r.resource(attrs)
        out << result.replaceFirst(request.contextPath, "")
    }

    private cacheZul(url) {
        if (pageScope.variables.containsKey("cached") == false) {
            if (url == null) {
                url = "/${controllerName}/${actionName}.zul"
            }
            def zres = new ZulResponse(url, request, response, servletContext)
            if (pageScope.variables.containsKey("model") == false) {
                if (zres.status.ok) {
                    pageScope.model = zres.model
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
        if (error) {
            try {
                text = messageSource.getMessage(error, locale)
            } catch (NoSuchMessageException e) {
                if (error instanceof MessageSourceResolvable) {
                    text = error?.code
                } else {
                    text = error?.toString()
                }
            }
        } else if (attrs['code']) {
            def code = attrs['code']
            def args = attrs['args']
            def defaultMessage = (attrs['default'] != null ? attrs['default'] : code)

            def message = messageSource.getMessage(code,
                    args == null ? null : args.toArray(),
                    defaultMessage,
                    locale)
            if (message) {
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
