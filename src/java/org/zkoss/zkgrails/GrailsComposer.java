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
import org.zkoss.zkgrails.scaffolding.*;
import org.codehaus.groovy.grails.commons.GrailsApplication;

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

        try {
            Object scaffold =
                GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "scaffold");
            ApplicationContext ctx = SpringUtil.getApplicationContext();
            GrailsApplication app = (GrailsApplication) ctx.getBean(
                    GrailsApplication.APPLICATION_ID,
                    GrailsApplication.class);

           ScaffoldingTemplate template = (ScaffoldingTemplate) ctx.getBean(
                    "zkgrailsScaffoldingTemplate",
                    ScaffoldingTemplate.class);                    
            template.initComponents((Class<?>)scaffold, (Component)comp, app);
        } catch(BeansException e) {}
    }

}
