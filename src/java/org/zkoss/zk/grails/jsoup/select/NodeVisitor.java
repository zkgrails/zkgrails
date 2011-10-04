package org.zkoss.zk.grails.jsoup.select;

import org.zkoss.zk.ui.Component;

/**
 * Node visitor interface
 */
public interface NodeVisitor {
    public void head(Component node, int depth);

    public void tail(Component node, int depth);
}
