package org.zkoss.zk.grails;

import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.Page;

import org.zkoss.zk.ui.util.Composer;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.apache.commons.lang.StringUtils;

public class ComposerResolver {

    public static Composer resolveComposer(Page page, String name) {
        ServletContext servletContext = (ServletContext)page.getDesktop().getWebApp().getNativeContext();
        ApplicationContext ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        String[] result = name.split("\\.");
        String classNamePart = result[result.length-1];
        if (Character.isUpperCase(classNamePart.charAt(0))) {
            result[result.length-1] = StringUtils.uncapitalize(classNamePart);
            name = StringUtils.join(result, ".");
        }
        return (Composer)ctx.getBean(name);
    }

    public static void init() {
        ComponentInfo.setComposerResolver(ComposerResolver.class);
    }
}