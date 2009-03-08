package org.zkoss.zkgrails;

import org.zkoss.zhtml.Messagebox;

public class Helper {

    public static void alert(Object text) throws Throwable {
        Messagebox.show(text.toString(), "Alert", Messagebox.OK, Messagebox.QUESTION);
    }

}
