package org.zkoss.zk.grails.select;

import groovy.lang.Closure;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

public class JQueryEventListener implements EventListener<Event> {

    private Closure<?> handler;

    public Closure<?> getHandler() {
        return handler;
    }

    public void setHandler(Closure<?> handler) {
        this.handler = handler;
    }

    @Override
    public void onEvent(Event e) throws Exception {
        InvokerHelper.invokeClosure(handler, new Object[]{e});
    }

}
