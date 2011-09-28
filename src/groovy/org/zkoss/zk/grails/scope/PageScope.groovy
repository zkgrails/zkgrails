package org.zkoss.zk.grails.scope

import org.springframework.beans.factory.config.Scope
import org.springframework.beans.factory.ObjectFactory
import org.zkoss.zk.ui.Executions

class PageScope implements Scope {

    static String PREFIX = '$P$'

    @Override
    def get(String name, ObjectFactory of) {
        def page = Executions?.current?.currentPage
        def obj = page?.getAttribute(PREFIX + name)
        if(obj == null) {
            obj = of.getObject()
            page?.setAttribute(PREFIX + name, obj)
        }
        return obj
    }

    @Override
    String getConversationId() {
        return Executions?.current?.currentPage?.getId()
    }

    @Override
    void registerDestructionCallback(String name, Runnable callback) { }

    @Override
    def remove(String name) {
        def page = Executions?.current?.currentPage
        return page?.removeAttribute(PREFIX + name)
    }

    @Override
    Object resolveContextualObject(String key) { }

}