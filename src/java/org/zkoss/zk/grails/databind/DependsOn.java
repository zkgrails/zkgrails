package org.zkoss.zk.grails.databind;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface DependsOn {

    // can be both String or List
    String[] value();

}