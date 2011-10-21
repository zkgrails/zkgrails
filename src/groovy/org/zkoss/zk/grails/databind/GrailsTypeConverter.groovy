package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter

public class GrailsTypeConverter implements TypeConverter, java.io.Serializable {

    private static final long serialVersionUID = 2L

    @Override
    public Object coerceToUi(Object val, Component comp) {
        def map = comp.getAttribute(BINDING_ARGS)
        def forward = map['forward']
        if(forward)
            return forward(val)
        else
            return TypeConverter.IGNORE
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        def map = comp.getAttribute(BINDING_ARGS)
        def reverse = map['reverse']
        reverse(val)
        return TypeConverter.IGNORE
    }

}