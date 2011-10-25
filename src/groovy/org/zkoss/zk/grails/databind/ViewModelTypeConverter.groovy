package org.zkoss.zk.grails.databind

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component

@Singleton
class ViewModelTypeConverter implements TypeConverter {

    Object coerceToUi(Object val, Component comp) {
        if(val instanceof Map && val.size() == 2 &&
           val.containsKey("forward") && val.containsKey("reverse")) {
            def c = val['forward']
            return c.call()
        } else if (val instanceof Closure) {
            return val()
        }

        return val
    }

    Object coerceToBean(Object val, Component comp) {
        if(val instanceof Map && val.size() == 2 &&
           val.containsKey("forward") && val.containsKey("reverse")) {
            def c = val['reverse']
            c.call()
            return TypeConverter.IGNORE
        } else if (val instanceof Closure) { // it's forward, skip
            return TypeConverter.IGNORE
        }
        return val
    }

}
