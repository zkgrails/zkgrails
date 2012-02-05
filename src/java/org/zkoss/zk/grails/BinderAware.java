package org.zkoss.zk.grails;

import org.zkoss.bind.Binder;

public interface BinderAware {

	Binder getBinder();
	void setBinder(Binder binder);

}
