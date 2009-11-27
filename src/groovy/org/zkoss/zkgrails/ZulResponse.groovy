package org.zkoss.zkgrails

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.Writer
import javax.servlet.http.HttpServletResponseWrapper

public class ZulResponse {

    def model = [:]
    def status = [:]

    static sliceDefs = [
        ['head', ~/(?m)(?s)(?i)\A.*\Q<div class="zk"\E/ , '<div class="zk"' ],
        ['body', ~/(?m)(?s)(?i)\Q<div class="zk"\E.*>.*/, null]
    ]

    public ZulResponse(String urlStr, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        def respBuffer = new ResponseBuffer(response) as HttpServletResponse
        try {
            servletContext.getRequestDispatcher(urlStr).include(request, respBuffer)
        } catch (Exception ex) {
            println "ZulResponse('$urlStr') got err: $ex.message"
            status.ok = false
            status.exception = ex
            return
        }
        status.ok = true
        model.source = respBuffer.toString()
        def err=false
        sliceDefs.each{ name, regexp, cutoff ->
            def matchr = (model.source =~ regexp)
            if (matchr) {
                def part = matchr[0]

                if (cutoff && part.endsWith(cutoff))
                    part = part[0..(part.size()-cutoff.size())-1]?.trim()

                model[name] = "\n<!-- zul-$name start-->\n$part\n<!-- zul-$name end-->\n"
            } else {
                model[name] = "regexp $regexp not found"
                err = true
            }
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
