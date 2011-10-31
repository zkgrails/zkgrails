package org.zkoss.zk.grails.databind

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
public @interface DependsOn {

    // can be both String or List
    Object expressions()

}