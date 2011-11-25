package org.zkoss.zk.grails.test

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component

/**
 * Created by IntelliJ IDEA.
 * User: chanwit
 * Date: 10/24/11
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
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
