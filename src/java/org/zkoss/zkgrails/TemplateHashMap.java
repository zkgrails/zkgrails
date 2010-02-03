package org.zkoss.zkgrails;

import java.util.HashMap;

class TemplateHashMap extends HashMap {
    
    @Override
    public Object get(Object key) {
        Object result = super.get(key);
        if(result == null)
            return "${" + key.toString() + "}";
        else
            return result;
    }
    
}