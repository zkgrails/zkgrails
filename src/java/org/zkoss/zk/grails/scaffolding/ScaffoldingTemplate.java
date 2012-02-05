package org.zkoss.zk.grails.scaffolding;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.zkoss.zk.ui.Component;

public interface ScaffoldingTemplate {

    public static final String SCAFFOLDING_TEMPLATE = "zkgrailsScaffoldingTemplate";

    public void initComponents(Class<?> scaffoldClass,
                                Component window,
                                GrailsApplication grailsApplication);

}
