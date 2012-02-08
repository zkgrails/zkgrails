package org.zkoss.zk.grails.composer;

import groovy.lang.Script;

import org.zkoss.zk.ui.Component;

public class JQueryComposer extends GrailsComposer {

    private static final long serialVersionUID = 2276200084257293417L;

    private Class<?> innerComposer;

    public Class<?> getInnerComposer() {
        return innerComposer;
    }

    public void setInnerComposer(Class<?> innerComposer) {
        this.innerComposer = innerComposer;
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Script s = (Script) innerComposer.newInstance();
        s.setProperty("composer", this);
        s.run();
    }

}
