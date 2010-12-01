package org.zkoss.zkgrails.artefacts;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;

public class DefaultGrailsCometClass extends AbstractGrailsClass
    implements GrailsCometClass {

    public static final String COMET = "Comet";

    @SuppressWarnings("unchecked")
    public DefaultGrailsCometClass(Class clazz) {
        super(clazz, COMET);
    }

}
