package org.zkoss.zk.grails.livemodels;

import java.util.*;

public class LiveModelBuilder {

    private HashMap<String,Object> map;

    public LiveModelBuilder() {
        this.map = new HashMap<String,Object>();
    }

    public HashMap<String,Object> getMap() {
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

    public void domain(Class<?> domain) {
        this.map.put("domain", domain);
    }
}
