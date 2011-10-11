package org.zkoss.zk.grails.ui;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.http.SimpleUiFactory;
import org.zkoss.zk.ui.util.Composer;

/**
 * It provides a new composer object in custom way which is to get from Spring bean with pre-defined id naming rule.
 * If no such bean exists, it provides in original ZK's way.
 * This class should be used (configured in zk.xml) after ZK 6.0.0 that contains the new method:
 * public Composer newComposer(Class clazz, Page page)
 * @author Hawk
 *
 */
public class ComposerFactory extends SimpleUiFactory {

	@Override
	public Composer newComposer(Class clazz, Page page) {
		ServletContext servletContext = (ServletContext)page.getDesktop().getWebApp().getNativeContext();
		ApplicationContext ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		//TODO The following bean id naming convention (from class name) is used in plug-in descriptor and here.
		//it's better to re-factor it for reuse instead of copy-paste
		String className = clazz.getName();
		String[] result = className.split("\\.");
		String classNamePart = result[result.length-1];
		if (Character.isUpperCase(classNamePart.charAt(0))) {
			result[result.length-1] = StringUtils.uncapitalize(classNamePart);
			className = StringUtils.join(result, ".");
		}
		
		if (ctx.containsBean(className)){
			//check before getting beans to avoid runtime exception if failed
			return (Composer)ctx.getBean(className);
		}else{
			return super.newComposer(clazz, page);
		}
	}
}