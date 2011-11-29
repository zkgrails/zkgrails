package org.zkoss.zk.grails.web

import grails.util.GrailsNameUtils;
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.springframework.beans.BeansException
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.io.Resource

class ComposerMapping implements ApplicationContextAware, InitializingBean {

    private static final Log LOG = LogFactory.getLog(ComposerMapping.class)

    public static final String BEAN_NAME = "zkgrailsComposerMapping"

    ApplicationContext applicationContext
    GrailsApplication grailsApplication

    def map = [:]

    def refresh() {
        Resource[] resources = applicationContext.getResources("classpath:*/grails-app/zul/**/*.zul");
        for(Resource r: resources) {
            println r.getURL()
            def slurper = new XmlSlurper()
            slurper.setFeature('http://xml.org/sax/features/namespaces', false)
            def root = slurper.parse(r.getInputStream())
            def node = root.'**'.find {
                def attrs = it.attributes()
                if(attrs.containsKey('apply')) {
                    return attrs.get('apply') =~ '.*Composer'
                }
                return false
            }
            if(node) {
                def composerName = node.attributes().get('apply')
                LOG.info("Found ${composerName} : ${r.getURL()}")
                // TODO
                // map[node.'@apply']
                // println node.'@apply'
                // println r.filename
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refresh()
    }

    public String resolveZul(String composerPath) {
        //
        // map path to composer
        //
        def key = grailsApplication.composerClasses.find { it.logicalPropertyName == composerPath }.fullname
        return map[key]
    }

}
