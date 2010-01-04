package org.zkoss.zkgrails;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletContext;

import org.zkoss.zk.ui.Executions;
import org.zkoss.xel.VariableResolver;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>DelegatingVariableResolver, a spring bean variable resolver.</p>
 *
 * <p>It defines a variable called <code>springContext</code> to represent
 * the instance of <code>org.springframework.context.ApplicationContext</code>.
 * It also looks variables for beans defined in <code>springContext</code>.
 *
 * <p>Usage:<br>
 * <code>&lt;?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?&gt;</code>
 *
 * @author andrewho
 * @author chanwit
 */

public class DelegatingVariableResolver implements VariableResolver {
	protected ApplicationContext _ctx;
	
	/**
	 * Get the spring application context.
	 */
	protected ApplicationContext getApplicationContext() {
		if (_ctx != null)
			return _ctx;
			
		_ctx = SpringUtil.getApplicationContext();
		return _ctx;
	}
	
	/**
	 * Get the spring bean by the specified name.
	 */		
	public Object resolveVariable(String name) {
		if ("springContext".equals(name)) {
			return getApplicationContext();
		}
		try {
			return getApplicationContext().getBean(name);
		} catch (NoSuchBeanDefinitionException ex) {
			return new NameableNullObject(this, name);
		}
	}

}

