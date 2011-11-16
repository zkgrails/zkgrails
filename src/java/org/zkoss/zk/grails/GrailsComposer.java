/* GrailsComposer.java

Copyright (C) 2008-2011 Chanwit Kaewkasi

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package org.zkoss.zk.grails;

import groovy.lang.Closure;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.zkoss.util.Pair;
import org.zkoss.zk.grails.databind.Attr;
import org.zkoss.zk.grails.databind.Observable;
import org.zkoss.zk.grails.select.ComponentUtil;
import org.zkoss.zk.grails.select.Components;
import org.zkoss.zk.grails.select.Selector;
import org.zkoss.zk.grails.scaffolding.ScaffoldingTemplate;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class GrailsComposer extends GenericForwardComposer {

    private static final long serialVersionUID = -5307023773234300419L;
    private MessageHolder messageHolder = null;

    // inject
    private DesktopCounter desktopCounter;
    private Object viewModel;
    // component holder for Selector
    private Component root;

    public Component getRoot() { return root; }
    public void setRoot(Component root) { this.root = root; }

    public GrailsComposer() {
        //default is true
        super('_', true, true);
        try {
            if (!shallSkipZscriptWiring()) {
                Field ignoreZscript = GenericAutowireComposer.class.getDeclaredField("_ignoreZScript");
                Field ignoreXel = GenericAutowireComposer.class.getDeclaredField("_ignoreXel");
                ignoreZscript.setAccessible(true);
                ignoreXel.setAccessible(true);
                ignoreZscript.setBoolean(this, false);
                ignoreXel.setBoolean(this, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void binds(Component comp) {
        if(viewModel != null) {
            ((GrailsViewModel)viewModel).binds(comp);
        }
    }

    public void setViewModel(Object vm) {
        this.viewModel = vm;
    }

    public Object getViewModel() {
        return new Observable(this.viewModel, ((GrailsViewModel)this.viewModel).getBinder(), null);
    }

    public void setDesktopCounter(DesktopCounter dc) {
        this.desktopCounter = dc;
    }

    public DesktopCounter getDesktopCounter() {
        return this.desktopCounter;
    }

    public void activateDesktop() throws java.lang.InterruptedException {
        desktopCounter.activate(this.desktop);
    }

    public void deactivateDesktop() throws java.lang.InterruptedException {
        desktopCounter.deactivate(this.desktop);
    }

    public void enablePush() {
        desktopCounter.enablePush(this.desktop);
    }

    public void disablePush() {
        desktopCounter.disablePush(this.desktop);
    }

    public Desktop getDesktop() {
        return this.desktop;
    }

    public Page getPage() {
        return this.page;
    }

    public ZkBuilder getBuild() {
        ZkBuilder builder = new ZkBuilder();
        builder.setPage(page);
        return builder;
    }

    public MessageHolder getMessage() {
        if (messageHolder == null) {
            HttpServletRequest request = (HttpServletRequest) (this.desktop.getExecution().getNativeRequest());
            messageHolder = new MessageHolder(page, request);
        }
        return messageHolder;
    }

    public String message(String code) {
        return getMessage().getAt(code);
    }

    public String message(Map<?,?> map) {
        return getMessage().call(map);
    }

    public void injectComet() throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().endsWith("Comet")) {
                GrailsComet gc = (GrailsComet) InvokerHelper.getProperty(this, f.getName());
                gc.setGrailsComposer(this);
            }
        }
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        this.root = comp;
        injectComet();

        comp.addEventListener("onBookmarkChange", new org.zkoss.zk.ui.event.EventListener() {
            public void onEvent(Event event) throws Exception {
                BookmarkEvent be = (BookmarkEvent)event;
                String hashtag = be.getBookmark();
                if(hashtag.startsWith("!")) {
                    hashtag = hashtag.substring(1);
                }
                // TODO parse to be inputs
                InvokerHelper.invokeMethod(GrailsComposer.this, hashtag, new Object[]{});
            }
        });

        handleAfterComposeClosure(comp);
        handleScaffold(comp);

        wireSelectorBasedHandler(comp);
    }

    private Map<Pair, List<Method>> selectorBasedHandler = new HashMap<Pair, List<Method>>();
    public List<Method> getSelectorBasedHandler(Pair pair) {
        return selectorBasedHandler.get(pair);
    }

    public void wireSelectorBasedHandler(Component comp) {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Listen.class)) {
                Listen h = method.getAnnotation(Listen.class);
                String[] values = h.value();
                for (String v : values) {
                    int p = v.lastIndexOf(".");
                    // TODO throw a proper exception when String is not in a correct format
                    String pattern = v.substring(0, p);
                    String eventName = v.substring(p + 1);
                    Components components = Selector.select(pattern, comp);
                    for (Component c : components) {
                        c.addEventListener(eventName, this);
                        Pair key = new Pair(c, eventName);
                        List<Method> handlerMethods;
                        if (selectorBasedHandler.containsKey(key) == false) {
                            handlerMethods = new ArrayList<Method>();
                            selectorBasedHandler.put(key, handlerMethods);
                        } else {
                            handlerMethods = selectorBasedHandler.get(key);
                        }
                        handlerMethods.add(method);
                    }
                }
            }
        }
    }

    private static final Method[] EMPTY_METHODS = new Method[]{};
    private Method[] getHandlerMethod(Class<?> cls, Event event) {
        Method method = ComponentsCtrl.getEventMethod(cls, event.getName());
        if (method != null)
            return new Method[]{method};
        List<Method> result = selectorBasedHandler.get(new Pair(event.getTarget(), event.getName()));
        if (result == null)
            return EMPTY_METHODS;
        return result.toArray(new Method[result.size()]);
    }

    /**
     * <p>Overrides GenericEventListener to use InvokerHelper to call methods. Because of this the events are now
     * part of groovy's dynamic methods, e.g. metaClass.invokeMethod works for event methods. Without this the default java code
     * don't call the overriden invokeMethod</p>
     *
     * @param event Event object
     * @throws Exception
     */
    @Override
    public void onEvent(Event event) throws Exception {
        final Object controller = getController();
        final Method[] methods = getHandlerMethod(controller.getClass(), event);
        if (methods.length == 0) return;
        for (Method method : methods) {
            if (method != null) {
                if (method.getParameterTypes().length == 0) {
                    InvokerHelper.invokeMethod(controller, method.getName(), null);
                } else if (event instanceof ForwardEvent) { //ForwardEvent
                    final Class<?> paramcls = method.getParameterTypes()[0];
                    //paramcls is ForwardEvent || Event
                    if (ForwardEvent.class.isAssignableFrom(paramcls) || Event.class.equals(paramcls)) {
                        InvokerHelper.invokeMethod(controller, method.getName(), new Object[]{event});
                    } else {
                        do {
                            event = ((ForwardEvent) event).getOrigin();
                        } while (event instanceof ForwardEvent);
                        InvokerHelper.invokeMethod(controller, method.getName(), new Object[]{event});
                    }
                } else {
                    Annotation[][] anns = method.getParameterAnnotations();
                    // one parameter, and annotation-less
                    if(anns.length == 1 && anns[0].length == 0) {
                        InvokerHelper.invokeMethod(controller, method.getName(), new Object[]{event});
                    } else {
                        Object[] params = new Object[anns.length];
                        int i = 0;
                        for(Annotation[] paramAnno: anns) {
                            for(Annotation a: paramAnno) {
                                if(a instanceof Attr) {
                                    String attrName = ((Attr) a).value();
                                    params[i] = ComponentUtil.attr(event.getTarget(), attrName);
                                    break;
                                }
                            }
                            i++;
                        }
                        InvokerHelper.invokeMethod(controller, method.getName(), params);
                    }
                }
            }
        }
    }

    private void handleAfterComposeClosure(Component comp) {
        try {
            Object c = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "afterCompose");
            if (c instanceof Closure) {
                ((Closure<?>) c).call(comp);
            }
        } catch (BeansException e) { /* do nothing */ }
    }

    private void handleScaffold(Component comp) {
        try {
            ApplicationContext ctx = SpringUtil.getApplicationContext();

            Object scaffold =
                    GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "scaffold");
            if (scaffold != null) {
                GrailsApplication app = ctx.getBean(
                        GrailsApplication.APPLICATION_ID,
                        GrailsApplication.class);

                ScaffoldingTemplate template = ctx.getBean(
                        ScaffoldingTemplate.SCAFFOLDING_TEMPLATE,
                        ScaffoldingTemplate.class);

                if (scaffold instanceof Boolean) {
                    if (((Boolean) scaffold) == true) {
                        //
                        // Use this to find class name
                        // and cut "Composer" off.
                        //
                        String name = this.getClass().getName().replaceAll("Composer", "");

                        //
                        // Look for the domain class.
                        //
                        GrailsClass domainClass = app.getArtefact("Domain", name);
                        Class<?> klass = domainClass.getClazz();
                        template.initComponents(klass, comp, app);
                    }
                } else {
                    template.initComponents((Class<?>) scaffold, comp, app);
                }
            }
        } catch (BeansException e) { /* do nothing */}
    }

    /**
     * Issue #146 - Support for skip zscript wiring for better performance.
     * 1st look at variable skipZscriptWiring on composer
     * 2nd look at global config skipZscriptWiring on Config.
     * If none specified default to false (don't skip wiring zscript variables) maintaining backward compatibility
     * @return the check of ZScript wiring
     */
    private boolean shallSkipZscriptWiring() {
        boolean shallSkipZscriptWiring;
        Object skipZscriptWiringFromComposer = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "skipZscriptWiring");

        if (skipZscriptWiringFromComposer != null &&
                skipZscriptWiringFromComposer instanceof Boolean) {
            shallSkipZscriptWiring = (Boolean) skipZscriptWiringFromComposer;
        } else {
            shallSkipZscriptWiring = ZkConfigHelper.skipZscriptWiring();
        }

        if (shallSkipZscriptWiring) {
            return true;
        }
        return false;
    }

    public Components select(String query) {
        return Selector.select(query, root);
    }

    public Components select(String query, Iterable<Component> roots) {
        return Selector.select(query, roots);
    }

}
