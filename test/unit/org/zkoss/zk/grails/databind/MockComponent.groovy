package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.metainfo.ComponentDefinition
import org.zkoss.zk.ui.IdSpace
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.scripting.Namespace
import org.zkoss.zk.au.AuService
import org.zkoss.zk.ui.ext.ScopeListener

class MockComponent implements Component {

    String getWidgetClass() {
        return null
    }

    void setWidgetClass(String clsnm) {
    }

    ComponentDefinition getDefinition() {
        return null
    }
    IdSpace getSpaceOwner() {
        return null
    }
    String getId() {
        return "id"
    }

    void setId(String id) {
    }

    Desktop getDesktop() {
        return null
    }
    Page getPage() {
        return null
    }

    void setPage(Page page) {
      
    }

    void setPageBefore(Page page, Component refRoot) {
      
    }
    String getUuid() {
        return "uuid-1"
    }
    Component getFellow(String id) {
        return null
    }
    Component getFellowIfAny(String id) {
        return null
    }
    Collection getFellows() {
        return null
    }

    boolean hasFellow(String compId) {
        return false
    }

    Component getFellow(String id, boolean recurse) {
        return null
    }
    Component getFellowIfAny(String id, boolean recurse) {
        return null
    }

    boolean hasFellow(String id, boolean recurse) {
        return false
    }
    Component getNextSibling() {
        return null
    }
    Component getPreviousSibling() {
        return null
    }
    Component getFirstChild() {
        return null
    }
    Component getLastChild() {
        return null
    }
    Map getAttributes(int scope) {
        return null
    }
    Object getAttribute(String name, int scope) {
        return null
    }

    boolean hasAttribute(String name, int scope) {
        return false
    }
    Object setAttribute(String name, Object value, int scope) {
        return null
    }
    Object removeAttribute(String name, int scope) {
        return null
    }
    Map getAttributes() {
        return null
    }
    Object getAttribute(String name) {
        return null
    }

    boolean hasAttribute(String name) {
        return false
    }
    Object setAttribute(String name, Object value) {
        return null
    }
    Object removeAttribute(String name) {
        return null
    }
    Object getAttributeOrFellow(String name, boolean recurse) {
        return null
    }

    boolean hasAttributeOrFellow(String name, boolean recurse) {
        return false
    }

    void setVariable(String name, Object val, boolean local) {
      
    }

    boolean containsVariable(String name, boolean local) {
        return false
    }
    Object getVariable(String name, boolean local) {
        return null
    }

    void unsetVariable(String name, boolean local) {
      
    }
    String getStubonly() {
        return "false"
    }

    void setStubonly(String stubonly) {
      
    }
    Component getParent() {
        return this
    }

    void setParent(Component parent) {
      
    }
    List getChildren() {
        return null
    }
    Component getRoot() {
        return this
    }

    boolean isVisible() {
        return false
    }

    boolean setVisible(boolean visible) {
        return false
    }

    boolean insertBefore(Component newChild, Component refChild) {
        return false
    }

    boolean appendChild(Component child) {
        return false
    }

    boolean removeChild(Component child) {
        return false
    }

    void detach() {
      
    }
    String getMold() {
        return null
    }

    void setMold(String mold) {
      
    }

    boolean addEventListener(String evtnm, EventListener listener) {
        return false
    }

    boolean removeEventListener(String evtnm, EventListener listener) {
        return false
    }

    boolean isListenerAvailable(String evtnm, boolean asap) {
        return false
    }
    Iterator getListenerIterator(String evtnm) {
        return null
    }

    boolean addForward(String originalEvent, Component target, String targetEvent) {
        return false
    }

    boolean addForward(String originalEvent, String targetPath, String targetEvent) {
        return false
    }

    boolean addForward(String originalEvent, Component target, String targetEvent, Object eventData) {
        return false
    }

    boolean addForward(String originalEvent, String targetPath, String targetEvent, Object eventData) {
        return false
    }

    boolean removeForward(String originalEvent, Component target, String targetEvent) {
        return false
    }

    boolean removeForward(String originalEvent, String targetPath, String targetEvent) {
        return false
    }

    boolean isInvalidated() {
        return false
    }

    void invalidate() {
      
    }
    Namespace getNamespace() {
        return null
    }

    void applyProperties() {
      
    }
    String setWidgetListener(String evtnm, String script) {
        return null
    }
    String getWidgetListener(String evtnm) {
        return null
    }
    Set getWidgetListenerNames() {
        return null
    }
    String setWidgetOverride(String name, String script) {
        return null
    }
    String getWidgetOverride(String name) {
        return null
    }
    Set getWidgetOverrideNames() {
        return null
    }
    String setWidgetAttribute(String name, String value) {
        return null
    }
    String getWidgetAttribute(String name) {
        return null
    }
    Set getWidgetAttributeNames() {
        return null
    }

    void setAuService(AuService service) {
      
    }
    AuService getAuService() {
        return null
    }
    Object getAttribute(String name, boolean recurse) {
        return null
    }

    boolean hasAttribute(String name, boolean recurse) {
        return false
    }
    Object setAttribute(String name, Object value, boolean recurse) {
        return null
    }
    Object removeAttribute(String name, boolean recurse) {
        return null
    }

    boolean addScopeListener(ScopeListener listener) {
        return false
    }

    boolean removeScopeListener(ScopeListener listener) {
        return false
    }

    def value

}
