package org.zkoss.zk.grails.databind

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component

@Singleton
class ViewModelTypeConverter implements TypeConverter {

    Object coerceToUi(Object val, Component comp) {
        if(val instanceof Closure) {
            def holder = new GetSetHolder()
            val.delegate = holder
            val.resolveStrategy = Closure.DELEGATE_ONLY
            val.call()

            def context = comp.getAttribute(DataBinder.ZKGRAILS_BINDING_CONTEXT)
            def binder = context['binder']

            def c = holder.getter
            c.delegate = binder
            c.resolveStrategy = Closure.DELEGATE_FIRST
            return c.call()
        }

        return val
    }

    Object coerceToBean(Object val, Component comp) {
        def context = comp.getAttribute(DataBinder.ZKGRAILS_BINDING_CONTEXT)
        def binder = context['binder']
        def viewModel = context['viewModel']
        def expr = context['expr']
        if(!viewModel.metaClass.hasProperty(viewModel, expr)) {
            return val
        }
        def xval = viewModel."$expr"
        if(xval instanceof Closure) {
            def holder = new GetSetHolder()
            xval.delegate = holder
            xval.resolveStrategy = Closure.DELEGATE_ONLY
            xval.call()

            def c = holder.setter
            c.delegate = binder
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c.call(val)
            return TypeConverter.IGNORE
        }
        return val
    }

}
