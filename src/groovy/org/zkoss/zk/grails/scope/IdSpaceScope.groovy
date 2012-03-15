package org.zkoss.zk.grails.scope

import org.springframework.beans.factory.config.Scope
import org.springframework.beans.factory.ObjectFactory
import org.zkoss.zk.ui.Executions

class IdSpaceScope implements Scope {

    static String PREFIX = '$S$'

    @Override
    def get(String name, ObjectFactory of) {
        def desktop = Executions?.current?.desktop
        def obj = desktop?.getAttribute(PREFIX + name)
        if(obj == null) {
            obj = of.getObject()
            desktop?.setAttribute(PREFIX + name, obj)
        }
        return obj
    }

    @Override
    String getConversationId() {
        return Executions?.current?.desktop?.getId()
    }

    @Override
    void registerDestructionCallback(String name, Runnable callback) { }

    @Override
    def remove(String name) {
        def desktop = Executions?.current?.desktop
        return desktop?.removeAttribute(PREFIX + name)
    }

    @Override
    Object resolveContextualObject(String key) { }

}