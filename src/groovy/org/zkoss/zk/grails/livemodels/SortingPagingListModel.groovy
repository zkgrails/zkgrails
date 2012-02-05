package org.zkoss.zk.grails.livemodels

import org.zkoss.zul.AbstractListModel
import org.zkoss.zul.FieldComparator
import org.zkoss.zul.event.ListDataEvent

class SortingPagingListModel extends AbstractListModel {

    HashMap map  = [:]

    int     pageSize
    String  sortingProp
    String  orderBy
    Boolean sorted
    Class   domain

    private List cache
    private int  cachedSize = -1
    private int  offset

    public void init() throws Exception {
        pageSize = map['pageSize']
        orderBy  = map['orderBy']
        sorted   = map['sorted']
        domain   = map['domain']
        cachedSize = -1
        cache    = null
    }

    public void refresh() throws Exception {
        init()
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1)
    }

    private List load(int offset, int max) {
        if (sorted) {
            return domain.list(offset: offset, max: max, sort:sortingProp, order:orderBy)
        } else {
            return domain.list(offset: offset, max: max)
        }
    }

    @Override
    public Object getElementAt(int index) {
        if (cache == null || index < offset || index >= offset + pageSize) {
            offset = index
             cache = load(index, pageSize)
        }
        return cache.get(index-offset)
    }

    @Override
    public int getSize() {
        if (cachedSize < 0) {
            cachedSize = domain.count()
        }
        return cachedSize
    }

    public boolean remove(int index) {
        def u = getElementAt(index)
        if (u != null) {
            try {
                u.delete(flush:true)
                cache.remove(index-offset)
                return true
            } catch(org.springframework.dao.DataIntegrityViolationException e) {
                return false
            }
        }
        return false
    }

    @Override
    public void sort(Comparator c, boolean ascending) {
        if (c instanceof FieldComparator) {
            cache = null
            sortingProp = c.getRawOrderBy()
            orderBy = "asc"
            if (ascending == false) {
                orderBy = "desc"
            }
            fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1)
        }
    }

}
