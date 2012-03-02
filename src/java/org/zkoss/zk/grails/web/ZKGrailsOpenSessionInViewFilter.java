package org.zkoss.zk.grails.web;

import org.codehaus.groovy.grails.commons.ApplicationAttributes;
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ZKGrailsOpenSessionInViewFilter extends OncePerRequestFilter {

    public static final String PERSISTENCE_INTERCEPTOR_ID = "persistenceInterceptor";

    private PersistenceContextInterceptor getPersistenceContextInterceptor() {
        PersistenceContextInterceptor result = null;
        ApplicationContext applicationContext = (ApplicationContext) getServletContext().getAttribute(ApplicationAttributes.APPLICATION_CONTEXT);
        if (applicationContext != null && applicationContext.containsBean(PERSISTENCE_INTERCEPTOR_ID)) {
            result = applicationContext.getBean(PERSISTENCE_INTERCEPTOR_ID, PersistenceContextInterceptor.class);
        }
        return result;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        PersistenceContextInterceptor interceptor = getPersistenceContextInterceptor();
        if(interceptor == null) {
            filterChain.doFilter(request, response);
            return;
        }

        interceptor.init();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (interceptor.isOpen()) {
                interceptor.flush();
                interceptor.destroy();
            }
        }
    }
}