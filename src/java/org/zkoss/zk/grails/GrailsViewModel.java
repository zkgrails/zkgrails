package org.zkoss.zk.grails;

import org.springframework.beans.factory.InitializingBean;
import org.zkoss.zkplus.databind.DataBinder;
import groovy.lang.Closure;
import org.zkoss.zk.grails.databind.BindingBuilder;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.zkoss.zk.ui.Component;

public class GrailsViewModel implements InitializingBean {

    private DataBinder binder = new DataBinder();

    private String id;

    public void setId(String value) { this.id = value; }
    public String getId()           { return this.id;  }
    
    public DataBinder getBinder()   { return this.binder; }


    @Override
    public void afterPropertiesSet() {

    }
    
    public void binds(Component root) {
        //
        // should be called by #afterCompose
        //

        //        
        //
        // binder.bindBean(this.getId(), this);

        //
        // get static binding
        //        
        Object obj = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "binding");
        if(obj instanceof Closure) {
            Closure c = ((Closure)obj);
            BindingBuilder bb = new BindingBuilder(this, this.getBinder(), root);
            c.setDelegate(bb);
            c.setResolveStrategy(Closure.DELEGATE_FIRST);
            c.call();
        }

        this.binder.loadAll();
    }
    
}