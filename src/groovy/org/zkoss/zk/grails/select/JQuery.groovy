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

}
