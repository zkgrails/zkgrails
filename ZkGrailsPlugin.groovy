import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zkplus.databind.BindingListModelList

class ZkGrailsPlugin {
    // the plugin version
    def version = "0.7.5"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]

    def loadAfter = ['hibernate']

    def artefacts = [
        org.zkoss.zkgrails.ComposerArtefactHandler,
        org.zkoss.zkgrails.FacadeArtefactHandler
    ]

    def watchedResources = ["file:./grails-app/composers/**/*Composer.groovy",
							"file:./plugins/*/grails-app/composers/**/*Composer.groovy",
                            "file:./grails-app/facade/**/*Facade.groovy",
							"file:./plugins/*/grails-app/facade/**/*Facade.groovy"]

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "chanwit"
    def authorEmail = "chanwit@gmail.com"
    def title = "ZK for Grails"
    def description = '''\\
Derived from Flyisland ZK Grails Plugin,
this plugin adds ZK Ajax framework (www.zkoss.org)
support to Grails applications.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/Zk+Plugin"

    def doWithSpring = {
		application.composerClasses.each { composerClass ->
            "${composerClass.propertyName}"(composerClass.clazz) { bean ->
                bean.scope = "prototype"
                bean.autowire = "byName"
            }
		}

		application.facadeClasses.each { facadeClass ->
            "${facadeClass.propertyName}"(facadeClass.clazz) { bean ->
                bean.scope = "session"
                bean.autowire = "byName"
            }
		}
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def doWithWebDescriptor = { xml ->
        def urls = ["*.zul", "*.zhtml", "*.svg", "*.xml2html"]

        // adding GrailsOpenSessionInView
        // TODO: if(manager?.hasGrailsPlugin("hibernate"))
        def filterElements = xml.'filter'[0]
        filterElements + {
            'filter' {
                'filter-name' ("GOSIVFilter")
                'filter-class' ("org.codehaus.groovy.grails.orm.hibernate.support.GrailsOpenSessionInViewFilter")
            }
        }
        // filter for each ZK urls
        def filterMappingElements = xml.'filter-mapping'[0]
        ["*.zul", "/zkau/*"].each {p ->
            filterMappingElements + {
                'filter-mapping' {
                    'filter-name'("GOSIVFilter")
                    'url-pattern'("${p}")
                }
            }
        }

        // quick hack for page filtering
        def pageFilter = xml.filter.find { it.'filter-name' == 'sitemesh'}
        pageFilter.'filter-class'.replaceBody('org.zkoss.zkgrails.ZKGrailsPageFilter')

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
        org.zkoss.zk.ui.AbstractComponent.metaClass.propertyMissing = { String name, handler ->
            if(name.startsWith("on") && handler instanceof Closure) {
                delegate.addEventListener(name, handler as EventListener)
            } else {
                throw new MissingPropertyException(name, delegate.class)
            }
        }

        org.zkoss.zk.ui.AbstractComponent.metaClass.append = { closure ->
            closure.delegate = new ZkBuilder(parent: delegate)
            closure.resolveStrategy = Closure.DELEGATE_FIRST
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
            def beanDefinitions = beans {
                "${composerClass.propertyName}"(composerClass.clazz) { bean ->
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
        }
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
