package org.zkoss.zk.grails.ui;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.http.SimpleUiFactory;
import org.zkoss.zk.ui.util.Composer;

/**
 * It provides a new composer object in custom way which is to get from Spring
 * bean with pre-defined id naming rule. If no such bean exists, it provides in
 * original ZK's way. This class should be used (configured in zk.xml) after ZK
 * 6.0.0 that contains the new method: public Composer newComposer(Class clazz,
 * Page page)
 *
 * @author Hawk
 * @author Chanwit
 *
 */
public class GrailsComposerFactory extends SimpleUiFactory {

	@Override
	public Composer<?> newComposer(Page page, String className) throws ClassNotFoundException {

		ServletContext servletContext = page.getDesktop().getWebApp().getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		String[] result = className.split("\\.");
		String classNamePart = result[result.length - 1];
		if (Character.isUpperCase(classNamePart.charAt(0))) {
			result[result.length - 1] = StringUtils.uncapitalize(classNamePart);
			className = StringUtils.join(result, ".");
		}

		if (ctx.containsBean(className)) {
			return (Composer<?>) ctx.getBean(className);
		} else {
			return super.newComposer(page, className);
		}

	}

}