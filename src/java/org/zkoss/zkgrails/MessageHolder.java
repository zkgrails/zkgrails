package org.zkoss.zkgrails;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.swing.event.ListSelectionEvent;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.MessageSourceResolvable;

import org.springframework.validation.ObjectError;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.beans.factory.BeanCreationException;

import org.zkoss.zk.ui.Page;

import java.util.*;

public class MessageHolder {

    private MessageSource messageSource;
    private HttpServletRequest request;

    public MessageHolder(Page page, HttpServletRequest request) {
        super();
        this.request = request;
        ServletContext servletContext = (ServletContext)page.getDesktop().getWebApp().getNativeContext();
        ApplicationContext ctx  = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        messageSource = (MessageSource)ctx.getBean("messageSource");
        if(messageSource == null) {
            throw new BeanCreationException("No bean [messageSource] found in ApplicationContext");
        }
    }

    public String getAt(String code) {
        return messageSource.getMessage(
                    code,
                    null,
                    RequestContextUtils.getLocale(request)
               );
    }

    public String call(Map map) {

        ObjectError error = (ObjectError)map.get("error");
        String code = (String)map.get("code");
        List args   = (List)map.get("args");

        if(error != null) {
            String text;
            try {
                text = messageSource.getMessage(error, RequestContextUtils.getLocale(request) );
            } catch (NoSuchMessageException e) {
                if (error instanceof MessageSourceResolvable) {
                    text = error.getCode();
                } else {
                    text = error.toString();
                }
            }
            return text;
        } else {
            Object[] a = (Object[]) args.toArray(new Object[args.size()]);
            return  messageSource.getMessage(code, a, RequestContextUtils.getLocale(request));
        }
    }
}
