package org.zkoss.zkgrails;

import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.grails.commons.spring.DefaultBeanConfiguration;
import org.codehaus.groovy.grails.commons.spring.ReloadAwareAutowireCapableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.zkoss.zk.ui.Executions;

class GrailsComet {

    GrailsComposer grailsComposer
    Thread th
    Boolean stop = false

    public void start() {
        final desktop = grailsComposer.desktop
        final page    = grailsComposer.page

        def trigger = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "trigger")

        final Long startDelay
        final Long delay
        final Long every

        if(trigger instanceof Number) {
            startDelay = 0L
            delay = (Number)trigger
        } else if(trigger instanceof Map) {
            startDelay = trigger['startDelay']
            def d = trigger['delay']
            if(!d)
                d = trigger['every']
            delay = d
        }
        if (desktop.isServerPushEnabled() == false) {
            desktop.enableServerPush(true)
        }
        final executeClosure = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "execute")
        if(executeClosure) {
            executeClosure.delegate = grailsComposer
            executeClosure.resolveStrategy = Closure.DELEGATE_FIRST
        }

        final beforeExecuteClosure = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "beforeExecute")
        if(beforeExecuteClosure) {
            beforeExecuteClosure.delegate = grailsComposer
            beforeExecuteClosure.resolveStrategy = Closure.DELEGATE_FIRST
        }

        final afterExecuteClosure  = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "afterExecute")
        if(afterExecuteClosure) {
            afterExecuteClosure.delegate = grailsComposer
            afterExecuteClosure.resolveStrategy = Closure.DELEGATE_FIRST
        }

        th = Thread.start {            
            if(beforeExecuteClosure) {
                Executions.activate(desktop)
                try {
                    beforeExecuteClosure.call(desktop, page)
                } finally {
                    Executions.deactivate(desktop)
                }
            }

            Thread.sleep(startDelay)
            while(!stop) {
                if(executeClosure) {
                    Executions.activate(desktop)
                    try {
                        executeClosure.call(desktop, page)
                    } finally {
                        Executions.deactivate(desktop)
                    }
                }
                Thread.sleep(delay)
            }
            if(afterExecuteClosure) {
                Executions.activate(desktop)
                try {
                    afterExecuteClosure.call(desktop, page)
                } finally {
                    Executions.deactivate(desktop)
                }
            }

            desktop.enableServerPush(false)                
        }
    }

    public void stop() {
        stop = true
    }

}
