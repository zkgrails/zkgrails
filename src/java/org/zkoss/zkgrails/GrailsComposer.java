package org.zkoss.zkgrails;

import org.zkoss.zkplus.spring.SpringUtil;
import java.lang.reflect.Method;
import org.springframework.context.ApplicationContext;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import groovy.lang.Closure;
import java.util.*;
import org.zkoss.zk.ui.Component;
import org.springframework.beans.*;

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
    
}
