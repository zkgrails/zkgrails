package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter

class Tuple {

    Component comp
    String attr
    String expr
    TypeConverter converter

    boolean equals(that) {
        if (this.is(that)) return true
        if (getClass() != that.class) return false

        Tuple tuple = (Tuple)that

        if (attr != tuple.attr) return false
        if (comp != tuple.comp) return false
        if (converter != tuple.converter) return false
        if (expr != tuple.expr) return false

        return true
    }

    int hashCode() {
        int result
        result = (comp != null ? comp.hashCode() : 0)
        result = 31 * result + (attr != null ? attr.hashCode() : 0)
        result = 31 * result + (expr != null ? expr.hashCode() : 0)
        result = 31 * result + (converter != null ? converter.hashCode() : 0)
        return result
    }
}
