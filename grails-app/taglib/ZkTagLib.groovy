import org.zkoss.zkgrails.ZulResponse
import org.zkoss.util.resource.Labels

class ZkTagLib {

    static namespace = "z"

    def head = { attrs, b ->
        cacheZul(attrs['zul'])
        out << pageScope.model['head']
    }

    def body = { attrs, b ->
        cacheZul(attrs['zul'])
        out << pageScope.model['body']
    }

    def l = { attrs, b ->
        String key = attrs["key"] as String
        List args = attrs["args"] as List
        Boolean required = attrs["required"] as Boolean
        String defValue = attrs["default"] as String
        def label

        if(!key) {
            throwTagError("Attribute [key] is obrigatory")
        }
        if(required != null && !(required instanceof Boolean)) {
            throwTagError("Attribute [required] has to be a boolean value [true|false]")            
        }
        if(args && !(args instanceof List)) {
            throwTagError("Attribute [args] has to be a list")
        }
        if(defValue && !(defValue instanceof String)) {
            throwTagError("Attribute [default] has to be a String")            
        }

        if(! required) {
            if(! defValue) {
                if(! args) {
                    label = Labels.getLabel(key)
                } else {
                    label = Labels.getLabel(key, args as Object[])
                }
            } else {
                if(args) {
                    label = Labels.getLabel(key, defValue, args as Object[])
                } else {
                    label = Labels.getLabel(key, defValue)
                }
            }
        } else {
            if(! args) {
                label = Labels.getRequiredLabel(key)
            } else {
                label = Labels.getRequiredLabel(key, args as Object[])
            }
        }

        out << label
    }

    private cacheZul(url) {            
        if(pageScope.variables.containsKey("cached")==false) {
            if(url==null) {
                url = "/${controllerName}/${actionName}.zul"
            }
            def zres = new ZulResponse(url, request, response, servletContext)
            if(pageScope.variables.containsKey("model")==false) {
                if(zres.status.ok) {
                    pageScope.model  = zres.model
                    pageScope.cached = true
                } else {
                    throw zres.status.exception
                }
            }
        } else {
            // do nothing
        }
    }
}
