package org.zkoss.zk.grails

import java.util.concurrent.ConcurrentHashMap
import org.codehaus.groovy.runtime.InvokerHelper
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.sys.ComponentsCtrl
import org.springframework.web.context.support.WebApplicationContextUtils
import org.zkoss.zk.ui.Component

class ZkBuilder {

    static Map<String,Component> ZKNODES = new ConcurrentHashMap();

    def page
    def parent
    def idComponents =[:]
    def z = null
    
    def resource(dir, file) {
        if(z == null) {
            def p = page
            if(!p) p = parent.page
            def ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(p.desktop.webApp.nativeContext)
            z = ctx.getBean('ZkTagLib')
        }        
        return z.resourceImpl(dir:dir, file: file)
    }

    boolean getTag(String tag) {
        if(ZKNODES.containsKey(tag))
            return true

        if(page == null)
            page = parent.page

        //
        // first try on LanguageDefinition
        // (might have custom/macro components on lang-addon.xml)
        //
        def comdef = page?.getLanguageDefinition()?.getComponentDefinitionIfAny(tag)

        //
        // if nothing on LanguageDefinition
        // then go via Page (zk default components)
        //
        if(!comdef) 
            comdef = page.getComponentDefinition(tag, true)

        def cls = comdef.resolveImplementationClass(page, null)
        if(cls == null)
            return false

        ZKNODES[tag] = cls
        return true
    }

    def methodMissing(String name, args) {
        if (!name.startsWith('on') && getTag(name)) {
            def zkObject
            Closure closure = null
            switch (args.length) {
                case 0:
                    zkObject = newInstance(name)
                    break;
                case 1:
                    def arg = args[0]
                    if (arg instanceof Closure) {
                        closure = arg
                        zkObject = newInstance(name)
                    } else if (arg instanceof String) {
                        zkObject = newInstance(name,[:], arg)
                    } else if (arg instanceof Map) {                        
                        zkObject = newInstance(name, arg)
                    } else {
                        throw new MissingMethodException(name, getClass(), args, false);
                    }
                    break;
                case 2:
                    def arg = args[0]
                    def arg2 = args[1]
                    if (arg2 instanceof String && arg instanceof Map) {
                        zkObject = newInstance(name, arg, arg2)
                    } else if (arg instanceof String && arg2 instanceof Closure) {
                        zkObject = newInstance(name,[:], arg)
                        closure = arg2
                    } else if (arg instanceof Map && arg2 instanceof Closure) {
                        zkObject = newInstance(name, arg)
                        closure = arg2
                    } else {
                        throw new MissingMethodException(name, getClass(), args, false);
                    }
                    break;
                case 3:
                    zkObject = newInstance(name, args[0], args[1])
                    closure = args[2]
                    break;
                default:
                    throw new MissingMethodException(name, getClass(), args, false);
            }

            if (closure) {
                def oldParent = parent
                parent = zkObject
                closure.delegate = this
                closure(zkObject)
                parent = oldParent
            }
            return zkObject
        }
        else if (!createEventListener(parent, name, args)) {
            throw new MissingMethodException(name, getClass(), args, false)
        }
        throw new MissingMethodException(name, getClass(), args, false)
    }

    private createEventListener(zkObject, String name, args) {
        def listener = InvokerHelper.asList(args)[0]
        if(listener instanceof Closure) {
            Closure cls = listener
            ZkBuilder zkBuilder = new ZkBuilder()
            zkBuilder.idComponents = idComponents
            cls.delegate = zkBuilder
        }
        if(name =~ /^on[A-Z]\w+/) {
            //
            // If listener is a closure then it's a server side listener
            // e.g. onClick { event -> doSomethingWithEvent }
            //
            if(listener instanceof Closure) {
                zkObject.addEventListener(name, listener as EventListener)
                return true
            }

            //
            // If listener is a String then it's a client side listener
            // e.g. onClick: "alert('Say hi from JS');"
            //
            else if(listener instanceof String) {
                zkObject.setWidgetListener(name, listener)
                return true
            }
        }

        //
        // Issue #100, Added support for attribute forward, which works 
        // exactly like in the .zul pages, just follow ZK spec to use it
        // e.g. intbox(forward: "onChange=onEventA")
        // Issue #108, MissingMethodException on applyForward(listener)
        //
        if(name == "forward" && listener instanceof String) {
            ComponentsCtrl.applyForward(zkObject, listener)
            return true
        }

        //
        // Issue #102, support the above format with a Map
        // e.g. intbox(forward:[onChange:"onEventA,onEventB"])
        // Issue #108, MissingMethodException on applyForward(listener)
        //
        else if(name == "forward" && listener instanceof Map) {
            def forwardString = listener.collect { k, v ->
                k + "=" + v
            }.join(",")
            ComponentsCtrl.applyForward(zkObject, forwardString)
            return true
        }
        return false
    }

    def propertyMissing(String name) {
        if (idComponents.containsKey(name)) {
            return idComponents[name]
        }
        throw new MissingPropertyException(name, getClass())
    }

    Object newInstance(String name, def args =[:], String id = null) {
        def use = args.remove("use")

        def zkObject = null
        if(use) {
            zkObject = (use as Class).newInstance()
        } else {
            zkObject = ZKNODES[name].newInstance()
        }
        attachId(id, zkObject)

        addAttributeEvents(zkObject, args)

        args.each {key, value ->
            zkObject[key] = value
        }
        if (parent) {
            zkObject.parent = parent
        }
        return zkObject
    }

    private def attachId(String id, zkObject) {
        if (id) {
            Class clazz = ZKNODES[id]
            if (clazz) {
                throw new IllegalArgumentException("Cannot use $id as an ID.  It clashes with $clazz")
            }
            zkObject.id = id
            idComponents[id] = zkObject
        }
    }

    void addAttributeEvents(zkObject, java.util.Map args) {
        def iter = args.keySet().iterator()
        while(iter.hasNext()) {
            def key = iter.next()
            if (createEventListener(zkObject, key, args[key]))
                iter.remove()
        }
    }
}
