package org.zkoss.web.util.resource;

import groovy.lang.Writable;
import groovy.text.Template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletContext;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.util.resource.Locator;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.metainfo.Parser;

public class CustomContentLoader extends ResourceLoader {

    private static final String GROOVY_PAGES_TEMPLATE_ENGINE = "groovyPagesTemplateEngine";
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String CONFIG_OPTION_GSP_ENCODING = "grails.views.gsp.encoding";
    private static final String CONFIG_ZKGRAILS_TAGLIB_DISABLE = "grails.zk.taglib.disabled";

    private final WebApp _wapp;
    private final ApplicationContext _ctx;

    public CustomContentLoader(WebApp wapp) {
        _wapp = wapp;
        _ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(
            (ServletContext)wapp.getNativeContext()
        );
    }

    protected Object parse(String path, File file, Object extra) throws Exception {
        GrailsApplication grailsApplication = (GrailsApplication)_ctx.getBean("grailsApplication");
        final Map<?, ?> config = grailsApplication.getConfig().flatten();

        Boolean disable = (Boolean) config.get(CONFIG_ZKGRAILS_TAGLIB_DISABLE);
        final Locator locator = extra != null ? (Locator)extra: PageDefinitions.getLocator(_wapp, path);

        if(disable != null) {
            if(disable) {
                return new Parser(_wapp, locator).parse(file, path);
            }
        }

        GroovyPagesTemplateEngine gsp = (GroovyPagesTemplateEngine)_ctx.getBean(GROOVY_PAGES_TEMPLATE_ENGINE);

        byte[] buffer = new byte[(int)file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(buffer);

        String encoding = (String)config.get(CONFIG_OPTION_GSP_ENCODING);
        if(encoding == null) {
            encoding = UTF_8_ENCODING;
        }

        String bufferStr = new String(buffer, encoding).replaceAll("@\\{", "\\$\\{'@'\\}\\{");

        // Issue 161
        // Issue 220
        Template template = gsp.createTemplate(new ByteArrayResource(bufferStr.getBytes(encoding)), false);

        //
        // Issue 113 is between here
        // Do not need to do anything, just correct encoding in Config.groovy
        //
        Writable w = template.make();
        StringWriter sw = new StringWriter();
        w.writeTo(new PrintWriter(sw));

        String zulSrc = sw.toString().replaceAll("\\#\\{","\\$\\{");

        StringReader reader  = new StringReader(zulSrc);
        PageDefinition pgdef = new Parser(_wapp, locator).parse(reader, Servlets.getExtension(path));
        pgdef.setRequestPath(path);
        return pgdef;
    }

    protected Object parse(String path, URL url, Object extra) throws Exception {
        final Locator locator = extra != null ? (Locator)extra: PageDefinitions.getLocator(_wapp, path);
        return new Parser(_wapp, locator).parse(url, path);
    }

}
