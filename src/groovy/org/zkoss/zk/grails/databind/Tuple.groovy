package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter

class Tuple {

    Component comp
    String attr
    String expr
    TypeConverter converter

    boolean equals(o) {
        if (this.is(o)) return true;
        if (getClass() != o.class) return false;

        Tuple tuple = (Tuple)o

        if (attr != tuple.attr) return false;
        if (comp != tuple.comp) return false;
        if (converter != tuple.converter) return false;
        if (expr != tuple.expr) return false;

        return true;
    }

    int hashCode() {
        int result;
        result = (comp != null ? comp.hashCode() : 0);
        result = 31 * result + (attr != null ? attr.hashCode() : 0);
        result = 31 * result + (expr != null ? expr.hashCode() : 0);
        result = 31 * result + (converter != null ? converter.hashCode() : 0);
        return result;
    }
}
