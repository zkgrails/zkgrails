package org.zkoss.zkgrails;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class DesktopCounter {
    
    private ConcurrentHashMap<Desktop, AtomicInteger> map = new ConcurrentHashMap<Desktop, AtomicInteger>();

    private AtomicInteger getOrCreate(Desktop d) {
        AtomicInteger i = map.get(d);
        if(i == null) {
            AtomicInteger newI = new AtomicInteger(0);
            i = map.putIfAbsent(d, newI);
            if (i == null) {
                i = newI;
            }
        }
        return i;        
    }
    
    public void enablePush(Desktop d) {
        AtomicInteger i = getOrCreate(d);
        int v = i.get();
        if(v == 0) {
            d.enableServerPush(true);
        }
    }
    
    public void disablePush(Desktop d) {
        AtomicInteger i = getOrCreate(d);
        int v = i.get();
        if(v == 0) {
            d.enableServerPush(false);
        }        
    }

    public void activate(Desktop d) throws java.lang.InterruptedException {
        synchronized(d) {
            AtomicInteger i = getOrCreate(d);
            int v = i.incrementAndGet();
            if(v == 1) {
                Executions.activate(d);
            }
        }
    }
    
    public void deactivate(Desktop d) throws java.lang.InterruptedException {
        synchronized(d) {
            AtomicInteger i = getOrCreate(d);
            int v = i.decrementAndGet();
            if(v == 0) {
                map.remove(d);
                Executions.deactivate(d);
            }
        }
    }
    
}