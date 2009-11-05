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

import org.zkoss.zkplus.spring.SpringUtil;
import java.lang.reflect.Method;
import org.springframework.context.ApplicationContext;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import groovy.lang.Closure;
import java.util.*;
import org.zkoss.zk.ui.Component;
import org.springframework.beans.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.sys.*;


public class GrailsComposer extends org.zkoss.zk.ui.util.GenericForwardComposer {
    
    public GrailsComposer() {
        super('_');
    }
    
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        try {
          Object c = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "afterCompose");
          if(c instanceof Closure) {
              ((Closure)c).call(comp);
          }
        } catch(BeansException e) {}
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
        final Method mtd = ComponentsCtrl.getEventMethod(controller.getClass(), evt.getName());
        if (mtd != null) {
            if (mtd.getParameterTypes().length == 0) {
                InvokerHelper.invokeMethod(controller, mtd.getName(), null);
            } else if (evt instanceof ForwardEvent) { //ForwardEvent
                final Class paramcls = (Class) mtd.getParameterTypes()[0];
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
