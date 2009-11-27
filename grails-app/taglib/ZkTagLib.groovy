import org.zkoss.zkgrails.ZulResponse

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
