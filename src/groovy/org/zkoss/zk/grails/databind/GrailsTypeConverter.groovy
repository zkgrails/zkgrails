package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter

public class GrailsTypeConverter implements TypeConverter, java.io.Serializable {

    private static final long serialVersionUID = 2L
    private static final String CURRENT_EXPR = "zkgrails.current.binding.expr"

    @Override
    public Object coerceToUi(Object val, Component comp) {
        def attr   = comp.getAttribute(CURRENT_EXPR)
        def binder = comp.getAttribute(BindingBuilder.BINDING_ARGS)['binder']
        def map    = comp.getAttribute(BindingBuilder.BINDING_ARGS)['closureSets']

        def forward = map["${attr}:forward"]
        def result
        if(forward)
            result = forward(val)
        else
            result = val

        //binder.loadAll()
        return result
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        def attr = comp.getAttribute(CURRENT_EXPR)
        def binder = comp.getAttribute(BindingBuilder.BINDING_ARGS)['binder']
        def map  = comp.getAttribute(BindingBuilder.BINDING_ARGS)['closureSets']

        def reverse = map["${attr}:reverse"]

        def result
        if(reverse == null)
            result = val
        else {
            reverse(val)
            result = TypeConverter.IGNORE
        }
        return result
    }

}