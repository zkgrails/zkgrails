package org.zkoss.zkgrails

import org.codehaus.groovy.grails.commons.ConfigurationHolder

//
// Helper class to get the Url Mapping in the ZKPageFilter and ZkGrailsPlugin
//
class ZkConfigHelper {

    //
    // Get the extensions configuration using the ConfigurationHolder
    // Return String[]{"zul"} if not configured
    //
    static ArrayList<String> getSupportExtensions() {
        def exts = ConfigurationHolder.config?.grails.zk.extensions
        if(exts) {
            return exts
        } else {
            return ["zul"]
        }
    }
}
