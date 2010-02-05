package org.zkoss.zkgrails

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.Writer
import javax.servlet.http.HttpServletResponseWrapper

public class ZulResponse {

    def model = [:]
    def status = [:]

    static head  = /(?m)(?s)(?i)(.*)<!-- ZK 5\.\d\.\d \d+ -->/
    static body  = /(?m)(?s)(?i)<!-- ZK 5\.\d\.\d \d+ -->(.*)/
    static kd = /<script>zkopt\(\{kd:1\}\);<\/script>/

    public ZulResponse(String urlStr, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        def respBuffer = new ResponseBuffer(response) as HttpServletResponse
        try {
            servletContext.getRequestDispatcher(urlStr).include(request, respBuffer)
        } catch (Exception ex) {
            // println "ZulResponse('$urlStr') got err: $ex.message"
            status.ok = false
            status.exception = ex
            return
        }
        status.ok = true
        model.source = respBuffer.toString()
        // println ("-" * 50)
        // println model.source
        def headResult = (model.source =~ head)
        // println ("-" * 50)
        // println headResult
        // println ("-" * 50)
        // println headResult[0]
        // println ("-" * 50)
        // println headResult[0][1]
        // println ("-" * 50)
        if(headResult?.groupCount()==1) {
            // remove title, so that we can customise it in GSP
            model['head'] = headResult[0][1]
            // .replaceAll(title, "")
        } else{
            // println ">> not match head"
            status.ok = false
        }

        // performance improvement, no further match if error occurred.
        if(status.ok == false) return

        def bodyResult = (model.source =~ body)
        if(bodyResult?.groupCount()==1) {
        	// model['body'] = bodyResult[0][1]
            model['body'] = bodyResult[0][1].replaceAll(kd, "")
        } else {
            // println ">> not match body"
            status.ok = false
        }
    }
}

public class ResponseBuffer extends HttpServletResponseWrapper {

    StringWriter sw = new StringWriter()
    PrintWriter writer = new PrintWriter(sw)

    public String toString() {sw.toString()}
    public PrintWriter getWriter() {writer}

    public ResponseBuffer(HttpServletResponse response) {
        super(response)
    }

}
