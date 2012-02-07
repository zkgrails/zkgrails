package org.zkoss.zk.grails.test

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component

class MockTypeConverter implements TypeConverter {

    def value

    Object coerceToUi(Object val, Component comp) {
        if(this.value) return this.value
        return val
    }

    Object coerceToBean(Object val, Component comp) {
        if(this.value) return this.value
        return val
    }

}
