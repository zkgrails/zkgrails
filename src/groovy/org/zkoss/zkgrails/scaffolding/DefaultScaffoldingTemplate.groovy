package org.zkoss.zkgrails.scaffolding

import org.zkoss.zkgrails.*
import org.codehaus.groovy.grails.scaffolding.*
import org.zkoss.zkplus.databind.DataBinder
import org.zkoss.zk.ui.event.ForwardEvent
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.zkoss.zk.ui.Component
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events

class DefaultScaffoldingTemplate implements ScaffoldingTemplate {

    def scaffold

    def placeHolder
    def binder = new DataBinder()

    def dc

    def scaffoldPaging
    def scaffoldListbox
    def scaffoldProps

    def selected

    def renderEditor(b, it, p, cp) {
        def old = b.parent
        b.parent = it
        def colwidth = 25
        def editor = null
        switch(p.type) {
            case String.class:
            case URL.class:
                editor = b.textbox(id: "fd_${p.name}", cols: colwidth)
                binder.addBinding(editor, "value", "selected.${p.name}")
                break
            case Integer.class:
                editor = b.intbox(id: "fd_${p.name}", cols: colwidth)
                binder.addBinding(editor, "value", "selected.${p.name}")
                break
            case Long.class:
                editor = b.longbox(id: "fd_${p.name}", cols: colwidth)
                binder.addBinding(editor, "value", "selected.${p.name}")
                break
            case Double.class:
            case Float.class:
                editor = b.doublebox(id: "fd_${p.name}", cols: colwidth)
                binder.addBinding(editor, "value", "selected.${p.name}")
                break
            case java.util.Date.class:
            case java.sql.Date.class:
            case Calendar.class:
                editor = b.datebox(id: "fd_${p.name}", cols: colwidth);
                binder.addBinding(editor, "value", "selected.${p.name}")
                break
            case java.sql.Time.class:
                editor = b.timebox(id: "fd_${p.name}", cols: colwidth);
                binder.addBinding(editor, "value", "selected.${p.name}")
                break
            case Boolean.class:
                editor = b.checkbox(id: "fd_${p.name}")
                binder.addBinding(editor, "checked", "selected.${p.name}")
                break
            case TimeZone.class:
            case Locale.class:
            case Currency.class:
                // select
                break
            case ([] as Byte[]).class:
            case ([] as byte[]).class:
                // byte array editor
                break
            default:
                if(p.isEnum()) {
                    println "enum"
                } else if(p.manyToOne || p.oneToOne) {
                    if(property.association) {
                        b.label(id: "fd_${f.name}", onClick:{
                            // FIXME: open bandded dialog
                        })
                        binder.addBinding(editor, "value", "selected.${p.name}")
                    }
                } else if((p.oneToMany && !p.bidirectional)
                         || (p.manyToMany && p.isOwningSide())) {
                    println "relation M-M"
                } else if(p.oneToMany) {
                    println "relation 1-M"
                }
        }
        b.parent = old
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
                    def count = 0
                    scaffoldProps.each { p ->
                        def cp = dc.constrainedProperties[p.name]
                        if(cp?.display==true && count < 6) {
                            count++
                            listcell(label: e[p.name])
                        }
                    }
                }
            }
        }
    }

    // TODO
    // x. wire events
    // x. add paginate
    // x. add annotation (if possible)
    // x. refactor it to another class,
    //    probably in to org.zkoss.zkgrails.scaffolding.DefaultScaffoldingTemplate
    // x. name to Name, createdDate to Created Date
    // 6. i18n
    // x. type mapping
    // 8. handle relations
    public void initComponents(Class<?> scaffold,
                                Component window,
                                GrailsApplication grailsApplication) {

        this.scaffold = scaffold

        dc = grailsApplication.getDomainClass(scaffold.name)
        placeHolder = window.getFellowIfAny("scaffoldingBox")

        def excludedProps = ['version',
                               Events.ONLOAD_EVENT,
                               Events.BEFORE_DELETE_EVENT,
                               Events.BEFORE_INSERT_EVENT,
                               Events.BEFORE_UPDATE_EVENT]
        scaffoldProps = (dc.properties as Object[]).findAll {
            !excludedProps.contains(it.name)
        }

        scaffoldProps = scaffoldProps.sort(new DomainClassPropertyComparator(dc))

        placeHolder.append {
            scaffoldListbox = listbox(id: "lst${scaffold.name}", multiple:true, rows: 10) {
                listhead {
                    def count = 0
                    scaffoldProps.each{ p ->
                        def cp = dc.constrainedProperties[p.name]
                        if(cp?.display==true && count < 6) {
                            count++
                            if(!p.isAssociation()) {
                                // TODO: enable sorting
                                listheader(label:"${p.naturalName}")
                            } else {
                                listheader(label:"${p.naturalName}")
                            }
                        }
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
                        if(p.name!="id") {
                            def cp = dc.constrainedProperties[p.name]
                            if(cp?.display)
                            row {
                                label(value:"${p.naturalName}:")
                                renderEditor(delegate, it, p, cp)
                            }}
                        }
                    }
                }
            }
        }

        scaffoldPaging.totalSize = scaffold.count()
        redraw()
    }

}