package org.zkoss.zk.grails;

import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.zkoss.zk.grails.composer.GrailsComposer;


class GrailsComet {

    GrailsComposer grailsComposer
    Thread th
    Boolean stop = false

    public void start() {
        stop = false
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
        grailsComposer.enablePush()
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
            synchronized (desktop) {
                if(beforeExecuteClosure) {
                    grailsComposer.activateDesktop()
                    try {
                        beforeExecuteClosure.call(desktop, page)
                    } finally {
                        grailsComposer.deactivateDesktop()
                    }
                }
            }

            Thread.sleep(startDelay)
            while(!stop) {
                synchronized (desktop) {
                    if(executeClosure) {
                        grailsComposer.activateDesktop()
                        try {
                            executeClosure.call(desktop, page)
                        } finally {
                            grailsComposer.deactivateDesktop()
                        }
                    }
                }
                Thread.sleep(delay)
            }
            synchronized (desktop) {
                if(afterExecuteClosure) {
                    grailsComposer.activateDesktop()
                    try {
                        afterExecuteClosure.call(desktop, page)
                    } finally {
                        grailsComposer.deactivateDesktop()
                    }
                }
            }
            grailsComposer.disablePush()
        }
    }

    public void stop() {
        stop = true
    }

}
