import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

import grails.util.Environment
import grails.util.GrailsUtil

import org.zkoss.zk.ui.event.EventListener

import org.zkoss.zk.grails.ComposerResolver
import org.zkoss.zk.grails.DesktopCounter
import org.zkoss.zk.grails.ZkBuilder
import org.zkoss.zk.grails.livemodels.LiveModelBuilder
import org.zkoss.zk.grails.ListboxModelDynamicMethods
import org.zkoss.zk.grails.artefacts.CometArtefactHandler
import org.zkoss.zk.grails.artefacts.ComposerArtefactHandler
import org.zkoss.zk.grails.artefacts.FacadeArtefactHandler
import org.zkoss.zk.grails.artefacts.LiveModelArtefactHandler

import org.zkoss.zk.grails.livemodels.SortingPagingListModel
import org.zkoss.zk.grails.ZkConfigHelper

class ZkGrailsPlugin {
    // the plugin version
    def version = "1.1.BUILD-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    def loadAfter = ['core', 'controllers']

    def artefacts = [
        CometArtefactHandler,
        ComposerArtefactHandler,
        FacadeArtefactHandler,
        LiveModelArtefactHandler,
    ]

    def watchedResources = ["file:./grails-app/composers/**/*Composer.groovy",
                            "file:./plugins/*/grails-app/composers/**/*Composer.groovy",
                            "file:./grails-app/comets/**/*Comet.groovy",
                            "file:./plugins/*/grails-app/comets/**/*Comet.groovy",
                            "file:./grails-app/facade/**/*Facade.groovy",
                            "file:./plugins/*/grails-app/facade/**/*Facade.groovy",
                            "file:./grails-app/livemodels/**/*LiveModel.groovy",
                            "file:./plugins/*/grails-app/livemodels/**/*LiveModel.groovy"]


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
        "grails-app/livemodels/**",
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
        // boolean enableReload = env.isReloadEnabled() || application.config.grails.gsp.enable.reload || (developmentMode && env == Environment.DEVELOPMENT)
        // boolean warDeployedWithReload = application.warDeployed && enableReload

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

        //
        // Registering UI-Model classes
        //
        application.liveModelClasses.each { modelClass ->
            def cfg = GCU.getStaticPropertyValue(modelClass.clazz, "config")
            if(cfg) {
                def lmb = new LiveModelBuilder()
                cfg.delegate = lmb
                cfg.resolveStrategy = Closure.DELEGATE_ONLY
                cfg.call()
                if (lmb.map['model'] == 'page') {
                    "${modelClass.propertyName}"(SortingPagingListModel.class) { bean ->
                        bean.scope = "prototype"
                        bean.autowire = "byName"
                        bean.initMethod = "init"
                        map = lmb.map.clone()
                    }
                }
            }
        }

        // composer resolver which directly resolves Spring Beans
        ComposerResolver.init()
    }

    def doWithApplicationContext = { applicationContext ->
    }

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
        def urls = supportExts.collect{ "*." + it } + ["*.dsp", "*.zhtml", "*.svg", "*.xml2html"]


        // quick hack for page filtering
        def pageFilter = xml.filter.find { it.'filter-name'.text() == 'sitemesh' }

        def grailsVersion = GrailsUtil.grailsVersion

        // Grails 1.3.x & Grails 2.0.x
        def pageFilterClass = "org.zkoss.zk.grails.ZKGrailsPageFilter"
        if (grailsVersion.startsWith("1.2") || grailsVersion.startsWith("1.1")) {
            pageFilterClass = "org.zkoss.zk.grails.ZKGrailsPageFilter12x"
        }
        if(grailsVersion.startsWith("2.0")) {
            pageFilter.'filter-class'.replaceNode {
                'filter-class'(pageFilterClass)
            }
        } else {
            pageFilter.'filter-class'.replaceBody(pageFilterClass)
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

            //
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
        } else if (application.isArtefactOfType(LiveModelArtefactHandler.TYPE, event.source)) {
            def context = event.ctx
            if (!context) {
                if (log.isDebugEnabled())
                    log.debug("Application context not found. Can't reload")
                return
            }
            def modelClass = application.addArtefact(LiveModelArtefactHandler.TYPE,
                                                     event.source)
            def cfg = GCU.getStaticPropertyValue(modelClass.clazz, "config")
            if(cfg) {
                def lmb = new LiveModelBuilder()
                cfg.delegate = lmb
                cfg.resolveStrategy = Closure.DELEGATE_ONLY
                cfg.call()
                if (lmb.map['model'] == 'page') {
                    def beanDefinitions = beans {
                        "${modelClass.propertyName}"(SortingPagingListModel.class) { bean ->
                            bean.scope = "prototype"
                            bean.autowire = "byName"
                            bean.initMethod = "init"
                            map = lmb.map.clone()
                        }
                    }
                    beanDefinitions.registerBeans(event.ctx)
                }
            }
        }
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
