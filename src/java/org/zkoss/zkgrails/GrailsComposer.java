/* GrailsComposer.java

Copyright (C) 2008, 2009 Chanwit Kaewkasi

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
package org.zkoss.zkgrails;

import groovy.lang.Closure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zkgrails.scaffolding.ScaffoldingTemplate;
import org.zkoss.zkplus.spring.SpringUtil;

import javax.servlet.http.HttpServletRequest;

public class GrailsComposer extends org.zkoss.zk.ui.util.GenericForwardComposer {

    private static final long serialVersionUID = -5307023773234300419L;
    private MessageHolder messageHolder = null;

    public GrailsComposer() {
        super('_');
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
        if(messageHolder == null) {
            HttpServletRequest request = (HttpServletRequest)(this.desktop.getExecution().getNativeRequest());
            messageHolder = new MessageHolder(page, request);
        }
        return messageHolder;
    }

    public String message(String code) {
        return getMessage().getAt(code);
    }

    public String message(Map map) {
        return getMessage().call(map);
    }

    public void injectComet() throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field f: fields) {
            if(f.getName().endsWith("Comet")) {
                GrailsComet gc = (GrailsComet)InvokerHelper.getProperty(this, f.getName());
                gc.setGrailsComposer(this);
            }
        }
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        injectComet();

        try {
          Object c = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "afterCompose");
          if(c instanceof Closure) {
              ((Closure)c).call(comp);
          }
        } catch(BeansException e) { /* do nothing */ }

        try {
            ApplicationContext ctx = SpringUtil.getApplicationContext();

            Object scaffold =
                GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "scaffold");
            if(scaffold != null) {
                GrailsApplication app = (GrailsApplication) ctx.getBean(
                        GrailsApplication.APPLICATION_ID,
                        GrailsApplication.class);

                ScaffoldingTemplate template = (ScaffoldingTemplate) ctx.getBean(
                        "zkgrailsScaffoldingTemplate",
                        ScaffoldingTemplate.class);

                if(scaffold instanceof Boolean) {
                    if(((Boolean)scaffold) == true) {
                        //
                        // Use this to find class name
                        // and cut "Composer" off.
                        //
                        String name = this.getClass()
                                        .getName()
                                        .replaceAll("Composer", "");

                        //
                        // Look for the domain class.
                        //
                        GrailsClass domainClass = app.getArtefact("Domain", name);
                        Class<?> klass = domainClass.getClazz();
                        template.initComponents(klass, (Component)comp, app);
                    }
                } else {
                    template.initComponents((Class<?>)scaffold, (Component)comp, app);
                }
            }
        } catch(BeansException e) { /* do nothing */}
    }


    /**
     * <p>Overrides GenericEventListener to use InvokerHelper to call methods. Because of this the events are now
     * part of groovy's dynamic methods, e.g. metaClass.invokeMethod works for event methods. Without this the default java code
     * don't call the overriden invokeMethod</p>
     *
     * @param evt
     * @throws Exception
     */
    @Override
    public void onEvent(Event evt) throws Exception {
        final Object controller = getController();
        final Method mtd =	ComponentsCtrl.getEventMethod(controller.getClass(), evt.getName());
        if (mtd != null) {
            if (mtd.getParameterTypes().length == 0) {
                InvokerHelper.invokeMethod(controller, mtd.getName(), null);
            } else if (evt instanceof ForwardEvent) { //ForwardEvent
                final Class<?> paramcls = (Class<?>) mtd.getParameterTypes()[0];
                //paramcls is ForwardEvent || Event
                if (ForwardEvent.class.isAssignableFrom(paramcls)
                    || Event.class.equals(paramcls)) {
                    InvokerHelper.invokeMethod(controller, mtd.getName(), new Object[] {evt});
                } else {
                    do {
                        evt = ((ForwardEvent)evt).getOrigin();
                    } while(evt instanceof ForwardEvent);
                    InvokerHelper.invokeMethod(controller, mtd.getName(), new Object[] {evt});
                }
            } else {
                InvokerHelper.invokeMethod(controller, mtd.getName(), new Object[] {evt});
            }
        }
    }
}
