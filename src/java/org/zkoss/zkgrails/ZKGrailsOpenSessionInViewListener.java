/* OpenSessionInViewListener.java

Copyright (C) 2006 Potix Corporation. All Rights Reserved.
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
/* 
History:
    Tue Sep  5 10:11:55     2006, Created by henrichen
*/
package org.zkoss.zkgrails;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.util.logging.Log;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.codehaus.groovy.grails.commons.ApplicationAttributes;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.zkoss.zkplus.spring.SpringUtil;

import org.hibernate.StaleObjectStateException;

import java.util.List;

/**
 * Listener to init and cleanup the hibernate session automatically, implement
 * the Hibernate's "Open Session In View" pattern without JTA support. This listener
 * is used with {@link HibernateUtil}, or it will not work.
 *
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate "OpenSessionInView" Listener&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.OpenSessionInViewListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author henrichen
 */
public class ZKGrailsOpenSessionInViewListener implements ExecutionInit, ExecutionCleanup {
	private static final Log log = Log.lookup(ZKGrailsOpenSessionInViewListener.class);

    private SessionFactory lookupSessionFactory() {        
        ApplicationContext context = SpringUtil.getApplicationContext();
        if(context != null) {
            return (SessionFactory) context.getBean("sessionFactory", SessionFactory.class);
        }
        return null;
    }

    private static SessionFactory sessionFactory = null;

    private Session getSession() throws DataAccessResourceFailureException {
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        session.setFlushMode(FlushMode.AUTO);
        return session;
    }

	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) {
	    if(sessionFactory == null) {
	        sessionFactory = lookupSessionFactory();
        }
		if (parent == null) { //the root execution of a servlet request
			log.debug("Starting a database transaction: "+exec);
			getSession().beginTransaction();
		}
	}

	//-- ExecutionCleanup --//
	public void cleanup(Execution exec, Execution parent, List errs) {
		if (parent == null) { //the root execution of a servlet request
			try {
				if (errs == null || errs.isEmpty()) {
				    //if(sessionFactory.getCurrentSession().getTransaction().isActive()) {
    					// Commit and cleanup
    					log.debug("Committing the database transaction: "+exec);
    					sessionFactory.getCurrentSession().getTransaction().commit();
				    //}
				} else {
					final Throwable ex = (Throwable) errs.get(0);
					if (ex instanceof StaleObjectStateException) {
						// default implementation does not do any optimistic concurrency
						// control; it simply rollback the transaction.
						handleStaleObjectStateException(exec, (StaleObjectStateException)ex);
					} else {
						// default implementation log the stacktrace and then rollback
						// the transaction.
						handleOtherException(exec, ex);
					}
				}
			} finally {
                Session session = sessionFactory.getCurrentSession();
                if(!FlushMode.MANUAL.equals(session.getFlushMode())) {
                    session.flush();
                }
				session.close();
			}
		}
	}

	/**
	 * <p>Default StaleObjectStateException handler. This implementation
	 * does not implement optimistic concurrency control! It simply rollback
	 * the transaction.</p>
	 *
	 * <p>Application developer might want to extends this class and override
	 * this method to do other things like compensate for any permanent changes
	 * during the conversation, and finally restart business conversation.
	 * Or maybe give the user of the application a chance to merge some of his
	 * work with fresh data... what can be done here depends on the applications
	 * design.</p>
	 *
	 * @param exec the exection to clean up.
	 * @param ex the StaleObjectStateException being thrown (and not handled) during the execution
	 */
	protected void handleStaleObjectStateException(Execution exec, StaleObjectStateException ex) {
		log.error("This listener does not implement optimistic concurrency control!");
		rollback(exec, ex);
	}

	/**
	 * <p>Default other exception (other than StaleObjectStateException) handler.
	 * This implementation simply rollback the transaction.</p>
	 *
	 * <p>Application developer might want to extends this class and override
	 * this method to do other things like compensate for any permanent changes
	 * during the conversation, and finally restart business conversation...
	 * what can be done here depends on the applications design.</p>
	 *
	 * @param exec the exection to clean up.
	 * @param ex the Throwable other than StaleObjectStateException being thrown (and not handled) during the execution
	 */
	protected void handleOtherException(Execution exec, Throwable ex) {
		// Rollback only
		ex.printStackTrace();
		rollback(exec, ex);
	}

	/**
	 * rollback the current session.
	 *
	 * @param exec the exection to clean up.
	 * @param ex the StaleObjectStateException being thrown (and not handled) during the execution
	 */
	private void rollback(Execution exec, Throwable ex) {
	    // sessionFactory = lookupSessionFactory();
		try {
			if (sessionFactory.getCurrentSession().getTransaction().isActive()) {
				log.debug("Trying to rollback database transaction after exception:"+ex);
				sessionFactory.getCurrentSession().getTransaction().rollback();
			}
		} catch (Throwable rbEx) {
			log.error("Could not rollback transaction after exception! Original Exception:\n"+ex, rbEx);
		}
	}
}
