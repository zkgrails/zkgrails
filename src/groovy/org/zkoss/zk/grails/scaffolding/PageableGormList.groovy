package org.zkoss.zk.grails.scaffolding

class PageableGormList extends AbstractList {

    Class gormClass
    Integer pageSize
    Object[] buffer
    Integer page

    //
    //    def list = scaffold.list(
    //                    offset: page * scaffoldPaging.pageSize,
    //                    max:    scaffoldPaging.pageSize
    //    )
    //

    PageableGormList(Class gormClass, Integer pageSize) {
        super()
        this.gormClass = gormClass
        this.pageSize  = pageSize
        this.buffer = new Object[pageSize]
        this.page = -1
    }

    public int size() {
        gormClass.count()
    }

    //
    // TODO Optimise later
    //
    public Object get(int index) {
        //
        // calculate page
        // if pageSize = 8, index = 7, then page = 0
        // if pageSize = 8, index = 8, then page = 1
        //
        int newPage = index / pageSize

        if(newPage != page) {
            this.page = newPage
            def list = gormClass.list(
                offset: page * pageSize,
                max: pageSize
            )
            list.inject(0) { i, e ->
                this.buffer[i] = e
                i + 1
            }
        }

        //
        // if pageSize = 8, index = 7, page = 0, then localIndex = 7
        // if pageSize = 8, index = 8, page = 1, then localIndex = 0
        //
        int localIndex = index % pageSize
        return this.buffer[localIndex]
    }

}