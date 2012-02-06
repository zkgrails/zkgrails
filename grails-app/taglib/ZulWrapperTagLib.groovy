import grails.util.Environment

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

import org.springframework.beans.factory.InitializingBean

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException

import org.zkoss.zk.grails.ZulResponse
import org.zkoss.util.resource.Labels
import org.zkoss.util.Locales

import org.zkoss.zk.fn.JspFns

class ZulWrapperTagLib implements ApplicationContextAware, InitializingBean {

    static namespace = "zul"

    ApplicationContext applicationContext
    GrailsPluginManager pluginManager

    public void afterPropertiesSet() {
        /*
        def config = applicationContext.getBean(GrailsApplication.APPLICATION_ID).config
        if (config.grails.views.enable.jsessionid instanceof Boolean) {
            useJsessionId = config.grails.views.enable.jsessionid
        }
        */
    }

    def wrapper = { attrs, b ->
        def url = attrs.remove('url')
        if (url == null) {
            url = "/${controllerName}/${actionName}.zul"
        }
        out << '''<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="-1" />
'''
        out << JspFns.outZkHtmlTags(servletContext, request, response, null)
        out << "\n"
        out << "</head>\n"
        out << "<body>\n"
        def zres = new ZulResponse(url, request, response, servletContext)
        out << zres.model['source'].replace(",style:'width:100%;',ct:true","")
        out << "</body>\n"
        out << "</html>"
    }

}
