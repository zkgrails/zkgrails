package org.zkoss.zk.grails.databind

class GetSetHolder {

    Closure getter
    Closure setter

    void get(Closure c) {
        getter = c
    }

    void set(Closure c) {
        setter = c
    }

}
