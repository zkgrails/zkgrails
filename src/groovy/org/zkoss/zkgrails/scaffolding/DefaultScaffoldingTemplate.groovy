package org.zkoss.zkgrails.scaffolding

import org.zkoss.zkgrails.*
import org.codehaus.groovy.grails.scaffolding.*
import org.zkoss.zkplus.databind.DataBinder
import org.zkoss.zk.ui.event.ForwardEvent
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.zkoss.zk.ui.Component;

class DefaultScaffoldingTemplate implements ScaffoldingTemplate {

    def scaffold

    def placeHolder
    def binder = new DataBinder()

    def scaffoldPaging
    def scaffoldListbox
    def scaffoldProps

    def selected    

    def getTagFromType(type) {
        def tag = "textbox"
        switch(type) {
            case Boolean.class: tag = "checkbox"; break
            case Integer.class: tag = "intbox"; break
            case Long.class:    tag = "longbox"; break
            case Double.class:
            case Float.class:   tag = "doublebox"; break
            case java.util.Date.class:
                                tag = "datebox"; break
        }
        return tag
    }

    def redrawForm() {
        binder.bindBean("selected", selected)
        binder.loadAll()
    }

    def redraw(page=0) {
        def list = scaffold.list(
                        offset: page * scaffoldPaging.pageSize,
                        max:    scaffoldPaging.pageSize
        )

        scaffoldListbox.clear()
        scaffoldListbox.append {
            list.each { e ->
                listitem(value: e) {
                     scaffoldProps.each { p ->
                        listcell(label: e[p.name])
                    }
                }
            }
        }
    }

    // TODO
    // 1. wire events
    // 2. add paginate
    // 3. add annotation (if possible)
    // 4. refactor it to another class,
    //    probably in to org.zkoss.zkgrails.scaffolding.DefaultScaffoldingTemplate
    // x. name to Name, createdDate to Created Date
    // 6. i18n
    // x. type mapping
    public void initComponents(Class<?> scaffold, 
                                Component window, 
                                GrailsApplication grailsApplication) {
                                    
        this.scaffold = scaffold

        def dc = grailsApplication.getDomainClass(scaffold.name)
        placeHolder = window.getFellowIfAny("placeHolder")

        scaffoldProps = (dc.properties as Object[]).findAll {
            it.name != "id" && it.name != "version"
        }
        scaffoldProps = scaffoldProps.sort(new DomainClassPropertyComparator(dc))
        if(scaffoldProps.size() > 6) scaffoldProps = scaffoldProps[0..5]

        placeHolder.append {
            scaffoldListbox = listbox(id: "lst${scaffold.name}", multiple:true, rows: 10) {
                listhead {
                    scaffoldProps.each { p ->
                        listheader(label:"${p.naturalName}")
                    }
                }
            }
            scaffoldListbox.onSelect = { e ->
                selected = scaffoldListbox.selectedItem.value
                redrawForm()
            }
            scaffoldPaging = paging(id:"pag${scaffold.name}", pageSize: 10, onPaging:{ e ->
                redraw(e.activePage)
            })
            groupbox {
                caption(label: "${scaffold.name}")
                hbox {
                    def w="65px"
                    button(id:"btnAdd",     label:"New",     width: w, onClick: { e ->
                        selected = scaffold.newInstance()
                        redrawForm()
                    })
                    button(id:"btnUpdate",  label:"Update",  width: w, onClick: { e ->
                        binder.saveAll()
                        selected = selected?.merge(flush: true)
                        if(selected.version==0) { // newly inserted
                            scaffoldPaging.totalSize = scaffold.count()
                            scaffoldPaging.activePage = scaffoldPaging.pageCount - 1
                        }
                        redraw(scaffoldPaging.activePage)
                    })
                    button(id:"btnDelete",  label:"Delete",  width: w, onClick: { e ->
                        selected?.delete(flush:true)
                        selected = null

                        redrawForm()

                        scaffoldPaging.totalSize = scaffold.count()
                        redraw(scaffoldPaging.activePage)
                    })
                    button(id:"btnRefresh", label:"Refresh", width: w, onClick: { e ->
                        redraw(scaffoldPaging.activePage)
                    })
                }
                separator(bar: true)
                grid {
                    rows {
                        scaffoldProps.each { p ->
                        row {
                            def tag = getTagFromType(p.type)
                            label(value:"${p.naturalName}:")
                            def editor
                            if(tag!="checkbox") {
                                editor = "$tag"(id: "fd${p.name}", cols: 25)
                                binder.addBinding(editor, "value", "selected.${p.name}")
                            } else {
                                editor = "$tag"(id: "fd${p.name}")
                                binder.addBinding(editor, "checked", "selected.${p.name}")
                            }
                        }}
                    }
                }
            }
        }

        scaffoldPaging.totalSize = scaffold.count()
        redraw()
    }        

}