/* ZKGrailsPageFilter.java

Copyright (C) 2008, 2009 Chanwit Kaewkasi

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package org.zkoss.zkgrails;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.webapp.ContainerTweaks;
import com.opensymphony.sitemesh.webapp.ContentBufferingResponse;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.codehaus.groovy.grails.web.util.WebUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.codehaus.groovy.grails.web.sitemesh.*;

/**
 * Extends the default page filter to overide the apply decorator behaviour
 * if the page is a GSP
 *
 * @author Graeme Rocher
 * @author Chanwit Kaewkasi
 * @since Apr 19, 2006
 */
public class ZKGrailsPageFilter extends SiteMeshFilter {

    private static final Log LOG = LogFactory.getLog( ZKGrailsPageFilter.class );

    private static final String ALREADY_APPLIED_KEY = "com.opensymphony.sitemesh.APPLIED_ONCE";
    private static final String HTML_EXT = ".html";
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String CONFIG_OPTION_GSP_ENCODING = "grails.views.gsp.encoding";

    private FilterConfig filterConfig;
    private ContainerTweaks containerTweaks;

    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
        this.filterConfig = filterConfig;
        this.containerTweaks = new ContainerTweaks();
        FactoryHolder.setFactory(Factory.getInstance(new Config(filterConfig)));
    }

    public void destroy() {
        super.destroy();
        FactoryHolder.setFactory(null);
    }
    
    private String extractRequestPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();
        return (servletPath == null ? "" : servletPath)
                + (pathInfo == null ? "" : pathInfo)
                + (query == null ? "" : ("?" + query));
    }    

    private boolean isZK(HttpServletRequest request) {
        String path = extractRequestPath(request);
        final String[] ext = new String[]{".zul",".dsp","*.zhtml", "*.svg", "*.xml2html"};
        for(int i=0;i < ext.length; i++) {
            if(path.lastIndexOf(ext[i])!=-1) return true;
        }
        if(path.indexOf("/zkau")!=-1) return true;
        return false;
    }

    /*
     * TODO: This method has been copied from the parent to fix a bug in sitemesh 2.3. When sitemesh 2.4 is release this method and the two private methods below can removed

     * Main method of the Filter.
     *
     * <p>Checks if the Filter has been applied this request. If not, parses the page
     * and applies {@link com.opensymphony.module.sitemesh.Decorator} (if found).
     */
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) rq;
        HttpServletResponse response = (HttpServletResponse) rs;
        ServletContext servletContext = filterConfig.getServletContext();

        SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, servletContext);
        ContentProcessor contentProcessor = initContentProcessor(webAppContext);
        DecoratorSelector decoratorSelector = initDecoratorSelector(webAppContext);

        if(isZK(request)) {
            chain.doFilter(request, response);
            return;
        }

        if (filterAlreadyAppliedForRequest(request)) {
            // Prior to Servlet 2.4 spec, it was unspecified whether the filter should be called again upon an include().
            chain.doFilter(request, response);
            return;
        }

        if (!contentProcessor.handles(webAppContext)) {
            // Optimization: If the content doesn't need to be processed, bypass SiteMesh.
            chain.doFilter(request, response);
            return;
        }


        if (containerTweaks.shouldAutoCreateSession()) {
            request.getSession(true);
        }


        try {

             Content content = obtainContent(contentProcessor, webAppContext, request, response, chain);

             if (content == null) {
                 return;
             }

             detectContentTypeFromPage(content, response);
             Decorator decorator = decoratorSelector.selectDecorator(content, webAppContext);
             decorator.render(content, webAppContext);

         } catch (IllegalStateException e) {
             // Some containers (such as WebLogic) throw an IllegalStateException when an error page is served.
             // It may be ok to ignore this. However, for safety it is propegated if possible.
             if (!containerTweaks.shouldIgnoreIllegalStateExceptionOnErrorPage()) {
                 throw e;
             }
         } catch (RuntimeException e) {
             if (containerTweaks.shouldLogUnhandledExceptions()) {
                 // Some containers (such as Tomcat 4) swallow RuntimeExceptions in filters.
                 servletContext.log("Unhandled exception occurred whilst decorating page", e);
             }
             throw e;
         } catch (ServletException e) {
             request.setAttribute(ALREADY_APPLIED_KEY, null);
             throw e;
         }

    }

    /**
      * Continue in filter-chain, writing all content to buffer and parsing
      * into returned {@link com.opensymphony.module.sitemesh.Page} object. If
      * {@link com.opensymphony.module.sitemesh.Page} is not parseable, null is returned.
      */
     private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext,
                                   HttpServletRequest request, HttpServletResponse response, FilterChain chain)
             throws IOException, ServletException {

         ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, contentProcessor, webAppContext) {
             public void sendError(int sc) throws IOException {
                 GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest();
                 try {
                     super.sendError(sc);
                 } finally {
                     WebUtils.storeGrailsWebRequest(webRequest);
                 }
             }

             public void sendError(int sc, String msg) throws IOException {
                 GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest();
                 try {
                     super.sendError(sc, msg);
                 } finally {
                     WebUtils.storeGrailsWebRequest(webRequest);
                 }

             }

         };

         setDefaultConfiguredEncoding(request, contentBufferingResponse);
         chain.doFilter(request, contentBufferingResponse);
         // TODO: check if another servlet or filter put a page object in the request
         //            Content result = request.getAttribute(PAGE);
         //            if (result == null) {
         //                // parse the page
         //                result = pageResponse.getPage();
         //            }
         webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
         return contentBufferingResponse.getContent();
     }

    private void setDefaultConfiguredEncoding(HttpServletRequest request, ContentBufferingResponse contentBufferingResponse) {
        UrlPathHelper urlHelper = new UrlPathHelper();
        String requestURI = urlHelper.getOriginatingRequestUri(request);
        // static content?
        if(requestURI.endsWith(HTML_EXT))    {
            String encoding = (String) ConfigurationHolder.getFlatConfig().get(CONFIG_OPTION_GSP_ENCODING);
            if(encoding == null) encoding = UTF_8_ENCODING;
            contentBufferingResponse.setContentType("text/html;charset="+encoding);
        }

    }

    private boolean filterAlreadyAppliedForRequest(HttpServletRequest request) {
        if (request.getAttribute(ALREADY_APPLIED_KEY) == Boolean.TRUE) {
            return true;
        } else {
        request.setAttribute(ALREADY_APPLIED_KEY, Boolean.TRUE);
        return false;
        }
    }

    private void detectContentTypeFromPage(Content page, HttpServletResponse response) {
         String contentType = page.getProperty("meta.http-equiv.Content-Type");
         if(contentType != null && "text/html".equals(response.getContentType())) {
             response.setContentType(contentType);
         }
     }

}
