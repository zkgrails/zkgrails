package org.zkoss.zk.grails.test;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.sys.IdGenerator;

public class TestIdGenerator implements IdGenerator {
    
    public String nextComponentUuid(Desktop desktop, Component comp) {
        int i = Integer.parseInt(desktop.getAttribute("Id_Num").toString());
        i++;// Start from 1
        desktop.setAttribute("Id_Num", String.valueOf(i));
        return "zk_comp_" + i;
    }

    public String nextDesktopId(Desktop desktop) {
        if (desktop.getAttribute("Id_Num") == null) {
            String number = "0";
            desktop.setAttribute("Id_Num", number);
        }
        return null;
    }

    public String nextPageUuid(Page page) {
        return null;
    }
}
