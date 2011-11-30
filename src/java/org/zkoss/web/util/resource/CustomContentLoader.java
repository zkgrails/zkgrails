package org.zkoss.web.util.resource;

import grails.util.Environment;
import groovy.lang.Writable;
import groovy.text.Template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.core.io.DefaultResourceLocator;
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.context.WebApplicationContext;
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

    private static final Log LOG = LogFactory.getLog(CustomContentLoader.class);

    private final WebApp webApp;
    private final ApplicationContext appCtx;
    private GrailsApplication grailsApplication;

    public CustomContentLoader(WebApp wapp) {
        webApp = wapp;
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext((ServletContext)wapp.getNativeContext());
        grailsApplication = ctx.getBean(GrailsApplication.APPLICATION_ID, GrailsApplication.class);
        appCtx = grailsApplication.getMainContext();
        appCtx.getBean("grailsResourceLocator", DefaultResourceLocator.class);
    }

    private class InternalResourceInfo {

        private String path;
        private ServletContextLocator extra;
        private File file;
        private URL url;

        InternalResourceInfo(Object src) {
            try {
                Field pathField = src.getClass().getDeclaredField("path");
                pathField.setAccessible(true);
                this.path = (String)pathField.get(src);
                Field extraField = src.getClass().getDeclaredField("extra");
                extraField.setAccessible(true);
                this.extra = (ServletContextLocator)extraField.get(src);
                Field fileField = src.getClass().getDeclaredField("file");
                fileField.setAccessible(true);
                this.file = (File)fileField.get(src);
                Field urlField = src.getClass().getDeclaredField("url");
                urlField.setAccessible(true);
                this.url = (URL)urlField.get(src);
            } catch (Throwable e) {
                LOG.warn("InternalResourceInfo", e);
            }
        }
    }

    @Override
    public Object load(Object src) throws Exception {
        final InternalResourceInfo si =new InternalResourceInfo(src);
        org.springframework.core.io.Resource springResource = null;
        if(!Environment.isWarDeployed()) {
            final String path = "file:./grails-app/zul" + si.path;
            springResource = grailsApplication.getMainContext().getResource(path);
            LOG.debug("Get Spring Resource from: " + path);
        } else {
            // support only containers that expand WAR files
            final String path = "file:/" + si.extra.getServletContext().getRealPath("WEB-INF") + "/grails-app/zul" + si.path;
            springResource = grailsApplication.getMainContext().getResource(path);
            LOG.debug("Get Spring Resource in WAR mode from: " + path);
        }

        if(springResource != null) {
            try {
                return parse(si.path, springResource, si.extra);
            } catch (Throwable e) {
                LOG.debug("Cannot parse ZUL from springResource", e);
            }
        }

        if (si.url != null) {
            LOG.debug("Load from URL: " + si.url );
            return parse(si.path, si.url, si.extra);
        }

        if (!si.file.exists()) {
            LOG.debug("File " + si.file + " not found");
            return null; //File not found
        }

        try {
            return parse(si.path, si.file, si.extra);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    private StringReader preprocessGSP(final Map<?, ?> config, long length, InputStream in) throws IOException, UnsupportedEncodingException {
        GroovyPagesTemplateEngine gsp = (GroovyPagesTemplateEngine)appCtx.getBean(GROOVY_PAGES_TEMPLATE_ENGINE);

        byte[] buffer = new byte[(int)length];
        BufferedInputStream bis = new BufferedInputStream(in);
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
        return reader;
    }


    private Object parse(String path, org.springframework.core.io.Resource resource, Object extra) throws Throwable {
        final Map<?, ?> config = grailsApplication.getConfig().flatten();
        Boolean disable = (Boolean) config.get(CONFIG_ZKGRAILS_TAGLIB_DISABLE);
        final Locator locator = extra != null ? (Locator)extra: PageDefinitions.getLocator(webApp, path);

        if(disable != null) {
            if(disable) {
                return new Parser(webApp, locator).parse(new InputStreamReader(resource.getInputStream()), path);
            }
        }

        StringReader reader = preprocessGSP(config, resource.contentLength(), resource.getInputStream());
        PageDefinition pgdef = new Parser(webApp, locator).parse(reader, Servlets.getExtension(path));
        pgdef.setRequestPath(path);
        return pgdef;
    }

    protected Object parse(String path, File file, Object extra) throws Exception {
        GrailsApplication grailsApplication = (GrailsApplication)appCtx.getBean("grailsApplication");
        final Map<?, ?> config = grailsApplication.getConfig().flatten();

        Boolean disable = (Boolean) config.get(CONFIG_ZKGRAILS_TAGLIB_DISABLE);
        final Locator locator = extra != null ? (Locator)extra: PageDefinitions.getLocator(webApp, path);

        if(disable != null) {
            if(disable) {
                return new Parser(webApp, locator).parse(file, path);
            }
        }

        StringReader reader = preprocessGSP(config, file.length(), new FileInputStream(file));
        PageDefinition pgdef = new Parser(webApp, locator).parse(reader, Servlets.getExtension(path));
        pgdef.setRequestPath(path);
        return pgdef;
    }

    protected Object parse(String path, URL url, Object extra) throws Exception {
        final Locator locator = extra != null ? (Locator)extra: PageDefinitions.getLocator(webApp, path);
        return new Parser(webApp, locator).parse(url, path);
    }

}
