package org.zkoss.zk.grails.databind

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component


class ViewModelTypeConverter implements TypeConverter {

    Object coerceToUi(Object val, Component comp) {
        if(val instanceof Map &&
           val.size() == 2 &&
           val.containsKey("forward") &&
           val.containsKey("reverse")) {
            def c = val['forward']
            return c.call()
        } else if (val instanceof Closure) {
            return val()
        }

        return val
    }

    Object coerceToBean(Object val, Component comp) {
        if(val instanceof Map &&
           val.size() == 2 &&
           val.containsKey("forward") &&
           val.containsKey("reverse")) {
            def c = val['reverse']
            return c.call()
        }

        return val
    }

}
