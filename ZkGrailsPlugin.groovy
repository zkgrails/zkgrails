import org.zkoss.zkgrails.*
import org.zkoss.zkgrails.artefacts.*
import org.zkoss.zk.ui.event.EventListener
import grails.util.Environment
import org.zkoss.zkgrails.scaffolding.DefaultScaffoldingTemplate
import grails.util.*

class ZkGrailsPlugin {
    // the plugin version
    def version = "1.0.4"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]

    def loadAfter = ['hibernate']

    def artefacts = [
        org.zkoss.zkgrails.artefacts.CometArtefactHandler,
        org.zkoss.zkgrails.artefacts.ComposerArtefactHandler,
        org.zkoss.zkgrails.artefacts.FacadeArtefactHandler
    ]

    def watchedResources = ["file:./grails-app/composers/**/*Composer.groovy",
                            "file:./plugins/*/grails-app/composers/**/*Composer.groovy",
                            "file:./grails-app/comets/**/*Comet.groovy",
                            "file:./plugins/*/grails-app/comets/**/*Comet.groovy",
                            "file:./grails-app/facade/**/*Facade.groovy",
                            "file:./plugins/*/grails-app/facade/**/*Facade.groovy"]

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/conf/Config.groovy",
        "grails-app/conf/BuildConfig.groovy",
        "grails-app/conf/SeleniumConfig.groovy",
        "grails-app/domain/zk/**",
        "grails-app/comets/**",
        "grails-app/controllers/zk/**",
        "grails-app/composers/**",
        "grails-app/facade/**",
        "grails-app/views/**",
        "grails-app/taglib/MyTagLib.groovy",
        "grails-app/i18n/*.properties",
        "web-app/css/**",
        "web-app/issue*",
        "web-app/js/**",
        "web-app/META-INF/**",
        "web-app/test/**",
        "web-app/WEB-INF/**",
        "web-app/images/skin/**",
        "web-app/images/*.ico",
        "web-app/images/grails_*",
        "web-app/images/leftnav_*",
        "web-app/images/sp*",
        "web-app/*.zul",
        "test/**"
    ]

    // TODO Fill in these fields
    def author = "chanwit"
    def authorEmail = "chanwit@gmail.com"
    def title = "ZKGrails: ZK plugin for Grails"
    def description = '''\\
Originated from Flyisland ZK Grails Plugin,
this plugin adds ZK Ajax framework (www.zkoss.org) support to Grails applications.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/zk"

    def doWithSpring = {

        boolean developmentMode = !application.warDeployed
        Environment env = Environment.current
        boolean enableReload = env.isReloadEnabled() || application.config.grails.gsp.enable.reload == true || (developmentMode && env == Environment.DEVELOPMENT)
        boolean warDeployedWithReload = application.warDeployed && enableReload

        // Registering desktopCounter
        desktopCounter(DesktopCounter.class) { bean ->
            bean.scope = "singleton"
            bean.autowire = "byName"
        }

        //
        // Registering Composer Beans
        //
        application.composerClasses.each { composerClass ->
            def composerBeanName = composerClass.propertyName
            if(composerClass.packageName) {
                composerBeanName = composerClass.packageName + "." + composerBeanName
            }
            "${composerBeanName}"(composerClass.clazz) { bean ->
                bean.scope = "prototype"
                bean.autowire = "byName"
            }

        }

        //
        // Registering Facade Beans
        //
        application.facadeClasses.each { facadeClass ->
            "${facadeClass.propertyName}"(facadeClass.clazz) { bean ->
                bean.scope = "session"
                bean.autowire = "byName"
            }
        }

        //
        // Registering Comet classes
        //
        application.cometClasses.each { cometClass ->
            "${cometClass.propertyName}"(cometClass.clazz) { bean ->
                bean.scope = "prototype"
                bean.autowire = "byName"
            }
        }

        if(!parentCtx?.containsBean("zkgrailsScaffoldingTemplate")) {
            zkgrailsScaffoldingTemplate(DefaultScaffoldingTemplate.class) { bean ->
                bean.scope = "prototype"
                bean.autowire = "byName"
            }
        }

        // composer resolver which directly resolves Spring Beans
        org.zkoss.zkgrails.ComposerResolver.init()
    }

    def doWithApplicationContext = { applicationContext ->
    }

    static final String GOSIV_CLASS =
        "org.codehaus.groovy.grails.orm.hibernate.support.GrailsOpenSessionInViewFilter"

    def doWithWebDescriptor = { xml ->
        //
        // e.g. ["zul"]
        //
        def supportExts = ZkConfigHelper.supportExtensions

        //
        // e.g. ["*.zul", "/zkau/*"]
        //
        def filterUrls = supportExts.collect{ "*." + it } + ["/zkau/*"]

        //
        // e.g. ["*.zul", "*.dsp", "*.zhtml", "*.svg", "*.xml2html"]
        //
        def urls = supportExts.collect{ "*." + it } +
                   ["*.dsp", "*.zhtml", "*.svg", "*.xml2html"]

        // adding GrailsOpenSessionInView
        if(manager?.hasGrailsPlugin("hibernate")) {
            def filterElements = xml.'filter'[0]
            filterElements + {
                'filter' {
                    'filter-name' ("GOSIVFilter")
                    'filter-class' (GOSIV_CLASS)
                }
            }
            // filter for each ZK urls
            def filterMappingElements = xml.'filter-mapping'[0]
            filterUrls.each {p ->
                filterMappingElements + {
                    'filter-mapping' {
                        'filter-name'("GOSIVFilter")
                        'url-pattern'("${p}")
                    }
                }
            }
        }

        // quick hack for page filtering
        def pageFilter = xml.filter.find { it.'filter-name' == 'sitemesh'}
        def grailsVersion = GrailsUtil.grailsVersion
        if(grailsVersion.startsWith("1.3")) {
            pageFilter.'filter-class'.replaceBody('org.zkoss.zkgrails.ZKGrailsPageFilter')
        } else if(grailsVersion.startsWith("1.2") || grailsVersion.startsWith("1.1")) {
            pageFilter.'filter-class'.replaceBody('org.zkoss.zkgrails.ZKGrailsPageFilter12x')
        }

        def listenerElements = xml.'listener'[0]
        listenerElements + {
            'listener' {
                'display-name' ("ZK Session Cleaner")
                'listener-class' ("org.zkoss.zk.ui.http.HttpSessionListener")
            }
        }

        def servletElements = xml.'servlet'[0]
        def mappingElements = xml.'servlet-mapping'[0]

        servletElements + {
            'servlet' {
                'servlet-name' ("zkLoader")
                'servlet-class' ("org.zkoss.zk.ui.http.DHtmlLayoutServlet")
                'init-param' {
                    'param-name' ("update-uri")
                    'param-value' ("/zkau")
                }
                'load-on-startup' (0)
            }
        }

        urls.each {p ->
            mappingElements + {
                'servlet-mapping' {
                    'servlet-name'("zkLoader")
                    'url-pattern'("${p}")
                }
            }
        }

        servletElements + {
            'servlet' {
                'servlet-name' ("auEngine")
                'servlet-class' ("org.zkoss.zk.au.http.DHtmlUpdateServlet")
            }
        }
        mappingElements + {
            'servlet-mapping' {
                'servlet-name'("auEngine")
                'url-pattern'("/zkau/*")
            }
        }
    }

    def doWithDynamicMethods = { ctx ->

        // Simpler way to add and remove event
        org.zkoss.zk.ui.AbstractComponent.metaClass.propertyMissing = { String name, handler ->
            if(name.startsWith("on") && handler instanceof Closure) {
                delegate.addEventListener(name, handler as EventListener)
            } else {
                throw new MissingPropertyException(name, delegate.class)
            }
        }

        // Simpler way to add and remove event
        org.zkoss.zk.ui.AbstractComponent.metaClass.methodMissing = {String name, args ->
            // converts OnXxxx to onXxxx
            name.metaClass.toEventName {return substring(indexOf("On"), length()).replace("On", "on")}

            if(name.startsWith("on") && args[0] instanceof Closure) {
                // register the new method to avoid methodMissing overhead
                org.zkoss.zk.ui.AbstractComponent.metaClass."${name}" { Closure listener ->
                    delegate.addEventListener(name, listener as EventListener)
                }
                delegate.addEventListener(name, args[0] as EventListener)
                return
            } else if (name.startsWith("addOn") && (args[0] instanceof Closure || args[0] instanceof EventListener)) {
                def eventName = name.toEventName()
                def listener = args[0] instanceof Closure ? args[0] as EventListener : args[0]
                org.zkoss.zk.ui.AbstractComponent.metaClass."${name}" { Closure handler ->
                    delegate.addEventListener(eventName, handler as EventListener)
                    handler as EventListener
                }
                // an overload version o add an EventListener directly
                org.zkoss.zk.ui.AbstractComponent.metaClass."${name}" << { EventListener evtListener ->
                    delegate.addEventListener(eventName, evtListener)
                    evtListener
                }
                delegate.addEventListener(eventName, listener)
                return listener
            } else if (name.startsWith("removeOn") && args[0] instanceof EventListener) {
                def eventName = name.toEventName()
                org.zkoss.zk.ui.AbstractComponent.metaClass."${name}" { EventListener listener ->
                    delegate.removeEventListener(eventName, listener)
                }
                return delegate.removeEventListener(eventName, args[0])
            } else {
                throw new MissingMethodException(name, delegate.class, args)
            }
        }

        org.zkoss.zk.ui.AbstractComponent.metaClass.append = { closure ->
            closure.delegate = new ZkBuilder(parent: delegate)
            closure.resolveStrategy = Closure.OWNER_FIRST
            closure.call()
        }

        org.zkoss.zul.Listbox.metaClass.clear = { ->
            while (delegate.itemCount > 0) {
                delegate.removeItemAt(0)
            }
        }

        org.zkoss.zul.Listbox.metaClass.setModel = { list ->
            ListboxModelDynamicMethods.setModel(delegate, list)
        }

        org.zkoss.zul.Listbox.metaClass.getModel = { ->
            delegate.getModel()
        }

        org.zkoss.zul.AbstractListModel.metaClass.getAt = { Integer i ->
            return delegate.getElementAt(i)
        }

        //
        // simple session
        //
        org.zkoss.zk.ui.http.SimpleSession.metaClass.getAt = { String name ->
            delegate.getAttribute(name)
        }
        org.zkoss.zk.ui.http.SimpleSession.metaClass.putAt = { String name, value ->
            delegate.setAttribute(name, value)
        }
        org.zkoss.zk.ui.http.SimpleSession.metaClass.propertyMissing = { String name ->
            delegate.getAttribute(name)
        }
        org.zkoss.zk.ui.http.SimpleSession.metaClass.propertyMissing << { String name, value ->
            delegate.setAttribute(name, value)
        }

        // Load specific components. Issue #103
        org.zkoss.zkplus.databind.DataBinder.metaClass.loadComponents = { List comps ->
            comps.each {
                delegate.loadComponent(it)
            }
        }
    }

    def onChange = { event ->
        if (application.isArtefactOfType(ComposerArtefactHandler.TYPE, event.source)) {
            def context = event.ctx
            if (!context) {
                if (log.isDebugEnabled())
                    log.debug("Application context not found. Can't reload")
                return
            }
            def composerClass = application.addArtefact(ComposerArtefactHandler.TYPE, event.source)
            def composerBeanName = composerClass.propertyName
            if(composerClass.packageName) {
                composerBeanName = composerClass.packageName + "." + composerBeanName
            }
            // composerBeanName = composerBeanName.replace('.', '_')
            def beanDefinitions = beans {
                "${composerBeanName}"(composerClass.clazz) { bean ->
                    bean.scope = "prototype"
                    bean.autowire = "byName"
                }
            }
            // now that we have a BeanBuilder calling registerBeans and passing the app ctx will
            // register the necessary beans with the given app ctx
            beanDefinitions.registerBeans(event.ctx)

            // Add the dynamic methods back to the class (since it's
            // effectively a completely new class).
            // event.manager?.getGrailsPlugin("zk")?.doWithDynamicMethods(event.ctx)
        } else if (application.isArtefactOfType(FacadeArtefactHandler.TYPE, event.source)) {
            def context = event.ctx
            if (!context) {
                if (log.isDebugEnabled())
                    log.debug("Application context not found. Can't reload")
                return
            }
            def facadeClass = application.addArtefact(FacadeArtefactHandler.TYPE, event.source)
            def beanDefinitions = beans {
                "${facadeClass.propertyName}"(facadeClass.clazz) { bean ->
                    bean.scope = "session"
                    bean.autowire = "byName"
                }
            }
            beanDefinitions.registerBeans(event.ctx)
        } else if (application.isArtefactOfType(CometArtefactHandler.TYPE, event.source)) {
            def context = event.ctx
            if (!context) {
                if (log.isDebugEnabled())
                    log.debug("Application context not found. Can't reload")
                return
            }
            def cometClass = application.addArtefact(CometArtefactHandler.TYPE, event.source)
            def beanDefinitions = beans {
                "${cometClass.propertyName}"(cometClass.clazz) { bean ->
                    bean.scope = "prototype"
                    bean.autowire = "byName"
                }
            }
            beanDefinitions.registerBeans(event.ctx)
        }
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
