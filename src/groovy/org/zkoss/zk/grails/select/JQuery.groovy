package org.zkoss.zk.grails.select

import java.util.List;

import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.EventListener;

public class JQuery {

    List<Component> components

    public JQuery() {
    }

    public JQuery(List<Component> comp) {
        this.components = comp;
    }

    def on(String eventName, Closure c) {
        components.each { comp ->
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c.delegate = comp
            def listener = new JQueryEventListener(handler: c)
            comp.addEventListener("on" + eventName.capitalize(), listener)
        }
        return this
    }

    String text() {
        def comp = components?.get(0)
        if(comp) {
            if(comp.hasProperty("text"))  return comp.text
            if(comp.hasProperty("label")) return comp.label
            if(comp.hasProperty("value")) return comp.value
        }
        return ""
    }

    def text(Object val) {
        components?.each { comp ->
            if(comp) {
                if(comp.hasProperty("text"))  {
                    comp.text  = val
                }
                if(comp.hasProperty("label")) {
                    comp.label = val
                }
                if(comp.hasProperty("value")) {
                    comp.value = val
                }
            }
        }
        return this
    }

    def append(Closure builder) {
        def comp = components?.get(0)
        if(comp) {
            comp.append builder
        }
        return this
    }

    def size() {
        return components?.size()
    }

    def getLength() {
        return components?.size()
    }

    def addClass(String styleClass) {
        if(!styleClass) return
        components.each { comp ->
            if(comp.sclass == null || comp.sclass.size() == 0) {
                comp.sclass = styleClass
            } else {
                comp.sclass = (comp.sclass + ' ' + styleClass).trim()
            }
        }
        return this
    }

    def removeClass(String styleClass) {
        if(!styleClass) return
        def inp = styleClass.split(' ')
        components.each { comp ->
            def styles = comp.sclass?.split(' ')
            if(!styles) {
                styles = []
            }
            comp.sclass = (styles - inp).join(' ')
        }
        return this
    }

    def attr(String name) {
        def comp = components?.get(0)
        if(comp) {
            return comp."$name"
        }
        return null
    }

    def attr(String name, def value) {
        components.each { comp ->
            comp."$name" = value
        }
        return this
    }

}
