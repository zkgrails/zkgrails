package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter

public class GrailsTypeConverter implements TypeConverter, java.io.Serializable {

    private static final long serialVersionUID = 2L
    private static final String CURRENT_ATTR = "zkgrails.current.binding.attr"

    @Override
    public Object coerceToUi(Object val, Component comp) {
        def attr = comp.getAttribute(CURRENT_ATTR)
        def map  = comp.getAttribute(BINDING_ARGS)['closureSets']
        def forward = map["${attr}:forward"]
        if(forward)
            return forward(val)
        else
            return TypeConverter.IGNORE
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        def attr = comp.getAttribute(CURRENT_ATTR)
        def map  = comp.getAttribute(BINDING_ARGS)['closureSets']
        def reverse = map["${attr}:reverse"]
        reverse(val)
        return TypeConverter.IGNORE
    }

}