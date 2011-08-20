package org.zkoss.zk.grails

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper
import org.apache.log4j.Logger


class ZulResponse {

    private static Logger LOG = Logger.getLogger(ZulResponse)

    def model = [:]
    def status = [:]

    static sliceDefs = [
        ['head', ~/(?m)(?s)(?i)\A.*\Q<!-- ZK 5\E/, '<!-- ZK 5'],
        ['body', ~/(?m)(?s)(?i)\Q<!-- ZK 5\E.*>.*/, null]
    ]

    public ZulResponse(String urlStr, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        long begin = System.currentTimeMillis()
        def respBuffer = new ResponseBuffer(response) as HttpServletResponse
        try {
            servletContext.getRequestDispatcher(urlStr).include(request, respBuffer)
        } catch (Exception ex) {
            status.ok = false
            status.exception = ex
            LOG.error "Failed to process url $urlStr. Exception: $ex"
            return
        }
        model.source = respBuffer.toString()
        /*
        status.ok = true
        model.source = respBuffer.toString()
        def err=false
        def start = model.source.indexOf("<!-- ZK 5")
        println ">>> ${model.source}"
        def stop  = start + 10
        while(model.source[stop] != '>') stop++
        model['head'] = model.source.substring(0, start)
        model['body'] = model.source.substring(stop)
        */
        /*
        sliceDefs.each{ name, regexp, cutoff ->
            def matchr = (model.source =~ regexp)

            if (matchr) {
                def part = matchr[0]

                if (cutoff && part.endsWith(cutoff)) {
                    int end = part.size() - cutoff.size() - 1
                    part = part[0..end]?.trim()
                }

                if(name == 'head') {
                    model[name] = "\n<!-- zul $name start-->\n$part\n<!-- zul $name end-->\n"
                } else name == 'body'  {
                    def javaScriptFirstDivOriginal = "style:'width:100%;',ct:true"
                    def javaScriptFirstDivHacked = "style:'width:100%;height:100%',ct:true"
                    model["_" + name] = "\n<!-- zul $name start-->\n$part\n<!-- zul $name end-->\n"
                    part = part.replace(javaScriptFirstDivOriginal, javaScriptFirstDivHacked)
                    model[name] = "\n<!-- zul $name start-->\n$part\n<!-- zul $name end-->\n"
                }
            } else {
                model[name] = "regexp $regexp not found\n\n\n$model.source"
                err = true
                LOG.error model[name]
            }
        }
        */
        //long end = System.currentTimeMillis()
        //println "Took ${end-begin} ms to process"
    }
}

public class ResponseBuffer extends HttpServletResponseWrapper {
    def sw = new StringWriter()
    def writer = new PrintWriter(sw)

    public String toString() {sw.toString()}
    public PrintWriter getWriter() {writer}

    public ResponseBuffer(HttpServletResponse response) {
        super(response)
    }
}
