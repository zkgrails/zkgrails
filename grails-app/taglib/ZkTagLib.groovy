import org.zkoss.zkgrails.ZulResponse
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH

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
    
    def iframeSupport = { attrs, body ->
        def name = attrs['name']
        def contextPath = SCH.servletContext.contextPath
        def path = request.getRequestURI().replace(contextPath,"")
        def queryString = request.queryString
        if(queryString != null && queryString != "") {
        	queryString = "?" + queryString 
        } else {
        	queryString = ""
        }

        out << """
        <script>
        	if(parent.onIframeURLChange) {
        		parent.onIframeURLChange(     
        			parent.document.getElementsByName('${name}')[0].id,
        			'${path}${queryString}'
        		);
    		}
        </script>\n"""
    }

}
