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
package org.zkoss.zk.grails.composer;

import groovy.lang.Closure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.zkoss.util.Pair;
import org.zkoss.zk.grails.DesktopCounter;
import org.zkoss.zk.grails.GrailsComet;
import org.zkoss.zk.grails.MessageHolder;
import org.zkoss.zk.grails.ZkBuilder;
import org.zkoss.zk.grails.scaffolding.ScaffoldingTemplate;
import org.zkoss.zk.grails.select.JQuery;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;

public class GrailsComposer extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = -5307023773234300419L;
    private MessageHolder messageHolder = null;

    // inject
    private DesktopCounter desktopCounter;
    // component holder for Selector
    private Component root;

    public Component getRoot() { return root; }
    public void setRoot(Component root) { this.root = root; }

    public GrailsComposer() {
        //default is true
        super('_', true, true);
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

        comp.addEventListener("onBookmarkChange", new org.zkoss.zk.ui.event.EventListener<Event>() {
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

        Selectors.wireVariables(comp, this, null);
        Selectors.wireEventListeners(comp, this);
    }

    private Map<Pair<Component, String>, List<Method>> selectorBasedHandler = new HashMap<Pair<Component, String>, List<Method>>();
    public List<Method> getSelectorBasedHandler(Pair<Component, String> pair) {
        return selectorBasedHandler.get(pair);
    }

    private static final Method[] EMPTY_METHODS = new Method[]{};
    private Method[] getHandlerMethod(Class<?> cls, Event event) {
        Method method = ComponentsCtrl.getEventMethod(cls, event.getName());
        if (method != null)
            return new Method[]{method};
        List<Method> result = selectorBasedHandler.get(new Pair<Component, String>(event.getTarget(), event.getName()));
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
                    }/* else {
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
                    }*/
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

    public JQuery select(Object[] args) {
        if(args.length == 1) {
            Object arg0 = args[0];
            if(arg0 instanceof String) {
                return new JQuery(Selectors.find(root, (String)arg0));
            } else if(arg0 instanceof Component) {
                ArrayList<Component> comps = new ArrayList<Component>();
                comps.add((Component)arg0);
                return new JQuery(comps);
            } else if(arg0 instanceof List) {
                return new JQuery((List<Component>)arg0);
            }
        } else if(args.length == 2) {
            Object arg0 = args[0];
            Object arg1 = args[1];
            if(arg0 instanceof String && arg1 instanceof Page) {
                throw new RuntimeException("NYI");
            }
        }
        return null;
    }

}
