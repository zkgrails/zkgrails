package org.zkoss.zkgrails;

import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.Page;

import org.zkoss.zk.ui.util.Composer;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ComposerResolver {

	public static Composer resolveComposer(Page page, String name) {
		ServletContext servletContext = (ServletContext)page.getDesktop().getWebApp().getNativeContext();
		ApplicationContext ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return (Composer)ctx.getBean(name);
	}

	public static void init() {
		ComponentInfo.setComposerResolver(ComposerResolver.class);
	}
}