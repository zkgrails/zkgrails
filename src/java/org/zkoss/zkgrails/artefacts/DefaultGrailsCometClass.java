package org.zkoss.zkgrails.artefacts;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

public class DefaultGrailsCometClass extends AbstractInjectableGrailsClass
    implements GrailsCometClass {

    public static final String COMET = "Comet";

    public DefaultGrailsCometClass(Class clazz) {
        super(clazz, COMET);
    }

}
