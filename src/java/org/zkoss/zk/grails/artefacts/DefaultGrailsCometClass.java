package org.zkoss.zk.grails.artefacts;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;

public class DefaultGrailsCometClass extends AbstractGrailsClass
    implements GrailsCometClass {

    public static final String COMET = "Comet";

    public DefaultGrailsCometClass(@SuppressWarnings("rawtypes") Class clazz) {
        super(clazz, COMET);
    }

}
