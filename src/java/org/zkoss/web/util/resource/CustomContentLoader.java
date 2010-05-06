package org.zkoss.web.util.resource;

import javax.servlet.ServletContext;
import org.zkoss.io.Files;
import java.io.*;
import java.util.*;
import java.net.URL;
import org.zkoss.util.resource.Locator;
import org.zkoss.zk.ui.metainfo.Parser;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.WebApp;

import org.codehaus.groovy.grails.web.pages.*;

import groovy.text.Template;
import groovy.lang.Writable;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ByteArrayResource;
import org.zkoss.zk.ui.metainfo.PageDefinition;

public class CustomContentLoader extends ResourceLoader {

    private final WebApp _wapp;
    private final ApplicationContext _ctx;

    public CustomContentLoader(WebApp wapp) {
        _wapp = wapp;
        _ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(
            (ServletContext)wapp.getNativeContext()
        );
    }

    //-- super --//
    protected Object parse(String path, File file, Object extra)
    throws Exception {
        Locator locator = null;
        if(extra !=null)
            locator = (Locator)extra;
        else
            locator = PageDefinitions.getLocator(_wapp, path);

        GroovyPagesTemplateEngine gsp = (GroovyPagesTemplateEngine)_ctx.getBean("groovyPagesTemplateEngine");
        
        // checked
        byte[] buffer = new byte[(int)file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(buffer);
        String bufferStr = new String(buffer, "UTF-8");
        bufferStr = bufferStr.replaceAll("@\\{", "\\$\\{'@'\\}\\{");        

        // checked
        Template template = gsp.createTemplate(new ByteArrayResource(bufferStr.getBytes("UTF-8")));

        //
        // Issue 113 is between here
        // Do not need to do anything, just correct encoding in Config.groovy
        //
        Writable w = template.make();
        StringWriter sw = new StringWriter();
        w.writeTo(new PrintWriter(sw));

        // checked        
        String zulSrc = sw.toString().replaceAll("\\#\\{","\\$\\{");

        // checked
        StringReader reader = new StringReader(zulSrc);
        PageDefinition pgdef = new Parser(_wapp, locator).parse(reader, null);
        pgdef.setRequestPath(path);
        return pgdef;
    }

    protected Object parse(String path, URL url, Object extra)
    throws Exception {
        final Locator locator =
            extra != null ? (Locator)extra: PageDefinitions.getLocator(_wapp, path);
        return new Parser(_wapp, locator).parse(url, path);		
    }
    
    public static void init() {
        PageDefinitions.setResourceLoaderClass(CustomContentLoader.class);
    }
}
