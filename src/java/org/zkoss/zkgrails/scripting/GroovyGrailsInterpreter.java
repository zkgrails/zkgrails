/* GroovyGrailsInterpreter.java

{{IS_NOTE
    Purpose:

    Description:

    History:
        Fri Feb  9 15:47:22     2007, Created by tomyeh
        June 2008, A lot of improvement, by Chanwit
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.
Copyright (C) 2008 Chanwit Kaewkasi

{{IS_RIGHT
    This program is distributed under GPL Version 2.0 in the hope that
    it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkgrails.scripting;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.context.ApplicationContext;
import org.zkoss.xel.Function;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.util.GenericInterpreter;
import org.zkoss.zk.ui.Page;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * Groovy interpreter.
 *
 * <p>
 * <a href="http://groovy.codehaus.org/">More about Groovy</a>.
 * <p>
 * <a href="http://grails.org/">Grails</a>
 *
 * @author tomyeh
 * @author Chanwit Kaewkasi
 */
public class GroovyGrailsInterpreter extends GenericInterpreter {

    private static GroovyClassLoader GCL;
    // TODO make this thread-safe ?
    private static HashMap<String, Class<?>> cachedScripts = new HashMap<String, Class<?>>();

    private Binding global;
    private GroovyShell ip;
    private String requestPath;

    public GroovyGrailsInterpreter() {
        if (GroovyGrailsInterpreter.GCL == null) {
            ApplicationContext ctx = SpringUtil.getApplicationContext();
            GrailsApplication app = (GrailsApplication) ctx.getBean(
                    GrailsApplication.APPLICATION_ID,
                    GrailsApplication.class);
            GroovyGrailsInterpreter.GCL = new GroovyClassLoader(app.getClassLoader());
        }
    }

    /**
     * Returns the top-level scope.
     */
    /* package */Binding getGlobalScope() {
        return global;
    }

    /**
     * Returns the native interpreter, or null if it is not initialized or
     * destroyed. From application's standpoint, it never returns null, and the
     * returned object must be an instance of {@link groovy.lang.GroovyShell}
     *
     * @since 3.0.2
     *
     * **/
    public Object getNativeInterpreter() {
        return ip;
    }

    /** {@link GenericInterpreter#}
     *
     * executing script
     *
     * **/
    protected void exec(String script) {
        String key = requestPath + script;
        if (cachedScripts.containsKey(key) == false) {
            Class<?> c = GroovyGrailsInterpreter.GCL.parseClass(Snippets.IMPORTS + script);
            cachedScripts.put(key, c);
        }

        Class<?> c = cachedScripts.get(key);
        Script scriptObject = InvokerHelper.createScript(c, global);
        scriptObject.run();
    }

    protected boolean contains(String name) {
        return global.getVariables().containsKey(name);
    }

    protected Object get(String name) {
        try {
            return global.getVariable(name);
        } catch (MissingPropertyException ex) {
            // Groovy throws exception instead of returning null
            return null;
        }
    }

    protected void set(String name, Object value) {
        global.setVariable(name, value);
    }

    protected void unset(String name) {
        global.getVariables().remove(name);
    }

    // Interpreter//
    public void init(Page owner, String zslang) {
        super.init(owner, zslang);
        requestPath = owner.getRequestPath();
        global = new Binding(new Variables());
        ip = new GroovyShell(GroovyGrailsInterpreter.GCL, global);
        exec(Snippets.ALERT);
    }

    /**
     * TODO: need to digg out a solution from groovy's manual public Class
     * getClass(String clsnm) { }
     */
    /**
     * Returns the method.
     * <p>
     * Currently it only looks for closures, and argTypes are ignored.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Function getFunction(String name, Class[] argTypes) {
        final Object val = get(name);
        if (!(val instanceof Closure))
            return null;
        return new ClosureFunction((Closure) val);
    }

    public void destroy() {
        ip = null;
        global = null;
        super.destroy();
    }

    // supporting class//
    /**
     * Extends Binding to support ZK namespaces.
     */
    private class Variables extends HashMap<Object, Object> {

        private static final long serialVersionUID = -5761536460396768587L;

        public Object get(Object key) {
            Object val = super.get(key);
            if (val != null || containsKey(key) || !(key instanceof String))
                return val;
            val = getFromNamespace((String) key);
            return val != UNDEFINED ? val : null;
        }
    }

    private static class ClosureFunction implements Function {
        private final Closure _closure;

        private ClosureFunction(Closure closure) {
            _closure = closure;
        }

        public Class<?>[] getParameterTypes() {
            return new Class[0];
        }

        public Class<Object> getReturnType() {
            return Object.class;
        }

        public Object invoke(Object obj, Object[] args) throws Exception {
            if (args == null)
                return _closure.call();
            else
                return _closure.call(args);
        }

        public Method toMethod() {
            return null;
        }
    }

}
