package org.zkoss.zkgrails.scaffolding;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.zkoss.zk.ui.Component;

public interface ScaffoldingTemplate {
    
    public void initComponents(Class<?> scaffoldClass, 
                                Component window, 
                                GrailsApplication grailsApplication);
}
