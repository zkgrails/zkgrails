package org.zkoss.zk.grails;

import groovy.lang.Closure;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.zkoss.zk.grails.databind.NewBindingBuilder;
import org.zkoss.zk.grails.databind.NewDataBinder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.GenericEventListener;

public class GrailsViewModel extends GenericEventListener {

    private NewDataBinder binder = new NewDataBinder();

    private String id;

    public void setId(String value)  { this.id = value; }
    public String getId()            { return this.id;  }
    
    public NewDataBinder getBinder() { return this.binder; }

    @Override
    public void onEvent(Event evt) throws Exception {
        System.out.println(evt.getName() + " " + evt.getTarget());
        Component comp = evt.getTarget();
        if(binder.contains(comp)) {
            binder.fireViewChanged(comp, evt.getName());
        }
    }

    //
    // should be called by #afterCompose
    //
    public void binds(Component root) {
        //
        // register event handlers
        // TODO check if this is propagate through children, it should
        //
        root.addEventListener("onChange", this);

        //
        // get static binding { }
        // and invoke to build binding
        //        
        Object obj = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(this, "binding");
        if(obj instanceof Closure) {
            Closure c = ((Closure)obj);
            NewBindingBuilder bb = new NewBindingBuilder(this, this.getBinder(), root);
            c.setDelegate(bb);
            c.setResolveStrategy(Closure.DELEGATE_FIRST);
            c.call();
        }

        binder.loadAll();
    }
    
}