package org.zkoss.zk.grails;

import groovy.lang.*;

import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.zkoss.zk.grails.databind.BindingBuilder;
import org.zkoss.zk.grails.databind.DataBinder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.GenericEventListener;

public class GrailsViewModel extends GenericEventListener {

    /**
     *
     */
    private static final long serialVersionUID = -6190273513182169664L;

    private DataBinder binder = new DataBinder(this);

    public static final String[] EXCLUDES = {"id", "class", "binder", "binding", "metaClass"};

    private String id;
    public void setId(String value)  { this.id = value; }
    public String getId()            { return this.id;  }

    public DataBinder getBinder() { return this.binder; }

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
            Closure<?> c = ((Closure<?>)obj);
            BindingBuilder bb = new BindingBuilder(this, this.getBinder(), root);
            c.setDelegate(bb);
            c.setResolveStrategy(Closure.DELEGATE_FIRST);
            c.call();

            bb.subscribeDependentExpressions();
        }

        binder.loadAll();
    }

    @Override
    public void onEvent(Event evt) throws Exception {
        Component comp = evt.getTarget();
        if(binder.containsComponent(comp)) {
            binder.fireViewChanged(comp, evt.getName());
        }
    }

}