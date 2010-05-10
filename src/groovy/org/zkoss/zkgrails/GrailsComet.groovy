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

        if(trigger instanceof Number) {
            startDelay = 0L
            delay = (Number)trigger
        } else if(trigger instanceof Map) {
            startDelay = trigger['startDelay']
            delay = trigger['delay']
        }
        if (desktop.isServerPushEnabled() == false) {
            desktop.enableServerPush(true)
        }
        final executeClosure = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "execute")
        executeClosure.delegate = grailsComposer
        executeClosure.resolveStrategy = Closure.DELEGATE_FIRST

        th = Thread.start {
            Thread.sleep(startDelay)
            while(!stop) {
                Executions.activate(desktop)
                try {
                    executeClosure.call(desktop, page)
                } finally {
                    Executions.deactivate(desktop)
                }
                Thread.sleep(delay)
            }
        }
    }

    public void stop() {
        stop = true
        final desktop = grailsComposer.desktop
        desktop.enableServerPush(false)
    }

}
