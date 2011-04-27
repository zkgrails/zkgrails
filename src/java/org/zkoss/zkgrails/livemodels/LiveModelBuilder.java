package org.zkoss.zkgrails.livemodels;

import java.util.*;

public class LiveModelBuilder {

    private HashMap map;

    public LiveModelBuilder() {
        this.map = new HashMap();
    }

    public HashMap getMap() {
        return this.map;
    }

    public void model(String s) {
        this.map.put("model", s);
    }

    public void pageSize(Integer i) {
        this.map.put("pageSize", i);
    }

    public void sorted(Boolean b) {
        this.map.put("sorted", b);
    }

    public void domain(Class domain) {
        this.map.put("domain", domain);
    }
}
