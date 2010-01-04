package org.zkoss.zkgrails;

import org.codehaus.groovy.runtime.NullObject;
import org.zkoss.xel.VariableResolver;

public class NameableNullObject extends NullObject implements Nameable {

	private VariableResolver resolver;
	private String name;

	public NameableNullObject(VariableResolver resolver, String name) {
		super();
		this.resolver = resolver;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getProperty(String property) {
		// TODO
		// this.name = this.name + "." + property;
		// return resolver.resolveVariable(this, this.name);
	}

}
