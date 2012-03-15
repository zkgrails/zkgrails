package org.zkoss.zk.grails.scaffolding

import grails.persistence.Event

import org.apache.commons.lang.StringUtils as SU

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.scaffolding.*

import org.zkoss.zk.grails.*
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zkplus.databind.DataBinder


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
        def old  = b.parent
        b.parent = it
        def colwidth = 25
        def editor   = null
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
                editor = b.datebox(id: "fd_${p.name}", cols: colwidth-4);
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
                    if(p.association) {
                        b.hbox {
                            editor = bandbox(id: "fd_${p.name}", cols: colwidth, readonly: true) {
                                bandpopup(width: "300px") {
                                vbox {
                                    listbox(mold: "paging",
                                            pagingPosition: "top",
                                            pageSize: 8,
                                            model: new PageableGormList(p.type, 8),
                                            onSelect: { ev ->
                                                editor.rawValue = ev.reference.value
                                                selected."${p.name}" = ev.reference.value
                                            }
                                    )
                                }}
                            }
                            toolbarbutton(label:"null", width:"30px", onClick:{
                                editor.rawValue = null
                                selected."${p.name}" = null
                            })
                        }
                        binder.addBinding(editor, "rawValue", "selected.${p.name}")
                    }
                } else if(   (p.oneToMany  && !p.bidirectional)
                          || (p.manyToMany &&  p.isOwningSide())) {
                    if(p.association) {
                        def bb  = null
                        def lst = null
                        b.vbox {
                            hbox {
                                bb = bandbox(id:"fd_${p.name}_bb", cols: colwidth, readonly: true) {
                                    bandpopup(width: "300px" ) {
                                        vbox {
                                            listbox(mold: "paging",
                                                    pagingPosition: "top",
                                                    pageSize: 8,
                                                    model: new PageableGormList(p.referencedPropertyType, 8),
                                                    onSelect: { ev ->
                                                        bb.rawValue = ev.reference.value
                                                    }
                                            )
                                        }
                                    }
                                }
                                toolbarbutton(label:"+", width:"24px", onClick:{
                                    selected."addTo${SU.capitalize(p.name)}"(bb.rawValue)
                                    lst.model = selected."${p.name}"
                                })
                                toolbarbutton(label:"-", width:"24px", onClick:{
                                    selected."removeFrom${SU.capitalize(p.name)}"(lst.selectedItem.value)
                                    lst.model = selected."${p.name}"
                                })
                            }
                            lst = listbox(id: "fd_${p.name}",
                                mold: "paging",
                                pagingPosition: "top",
                                pageSize: 8,
                                height:"155px",
                                width: "250px"
                            )
                        }
                        binder.addBinding(lst, "model", "selected.${p.name}")
                    }
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
                            def toDisplay=""
                            if(!p.isAssociation() || p.oneToOne || p.manyToOne) {
                                toDisplay = e[p.name]
                            } else {
                                if(e[p.name])
                                    toDisplay = "#${e[p.name].size()}"
                            }
                            listcell(label: toDisplay)
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
    // x. type mapping
    // x. handle relations
    //    1. 1-1
    //    2. 1-M
    // 6. i18n
    // 9. enable sorting

    public void initComponents(Class<?> scaffold,
                                Component window,
                                GrailsApplication grailsApplication) {

        this.scaffold = scaffold

        dc = grailsApplication.getDomainClass(scaffold.name)
        placeHolder = window.getFellowIfAny("scaffoldingBox")
        if (!placeHolder) return

        //
        // Hack to make vbox filled the parent
        //
        placeHolder.width = "100%"

        def pluginManager = grailsApplication.mainContext.pluginManager

        def excludedProps = Event.allEvents.toList() << 'version' << 'dateCreated' << 'lastUpdated'
        def persistentPropNames = dc.persistentProperties*.name
        boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate')
        if (hasHibernate && org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder.getMapping(dc)?.identity?.generator == 'assigned') {
            persistentPropNames << dc.identifier.name
        }
        scaffoldProps = dc.properties.findAll { persistentPropNames.contains(it.name) && !excludedProps.contains(it.name) }
        Collections.sort(scaffoldProps, new DomainClassPropertyComparator(dc))

        placeHolder.append {
            scaffoldListbox = listbox(id: "lst${scaffold.name}",
                height: "150px",
                multiple: true, rows: 8) {
                listhead {
                    def count = 0
                    scaffoldProps.each{ p ->
                        def cp = dc.constrainedProperties[p.name]
                        if(cp?.display==true && count < 6) {
                            count++
                            if(!p.isAssociation()) {
                                //
                                // TODO: enable sorting
                                //
                                listheader(label:"${p.naturalName}")
                            } else {
                                listheader(label:"${p.naturalName}")
                            }
                        }
                    }
                }
            }
            scaffoldListbox.onSelect = { e ->
                def id = scaffoldListbox?.selectedItem?.value.id
                selected = scaffold.get(id)
                redrawForm()
            }
            scaffoldPaging = paging(id:"pag${scaffold.name}", pageSize: 8, onPaging:{ e ->
                redraw(e.activePage)
            })
            separator()
            groupbox {
                caption(label: "${scaffold.name}")
                toolbar {
                    def w="75px"
                    toolbarbutton(id:"btnAdd",     label:"New",
                        image: resource('images', 'skin/database_add.png') , width: w, onClick: { e ->
                        selected = scaffold.newInstance()
                        redrawForm()
                    })
                    toolbarbutton(id:"btnUpdate",  label:"Update",
                        image: resource('images', 'skin/database_save.png'), width: w, onClick: { e ->
                        if(selected == null) {
                            selected = scaffold.newInstance()
                            binder.bindBean("selected", selected)
                        }
                        binder.saveAll()
                        selected = selected?.merge(flush: true)
                        if(selected.version==0) { // newly inserted
                            scaffoldPaging.totalSize  = scaffold.count()
                            scaffoldPaging.activePage = scaffoldPaging.pageCount - 1
                        }
                        redraw(scaffoldPaging.activePage)
                    })
                    toolbarbutton(id:"btnDelete",  label:"Delete",
                        image: resource('images', 'skin/database_delete.png'), width: w, onClick: { e ->
                        selected?.delete(flush:true)
                        selected = null

                        redrawForm()

                        scaffoldPaging.totalSize = scaffold.count()
                        redraw(scaffoldPaging.activePage)
                    })
                    toolbarbutton(id:"btnRefresh", label:"Refresh",
                        image: resource('images', 'skin/database_table.png'), width: w, onClick: { e ->
                        redraw(scaffoldPaging.activePage)
                    })
                }
                separator()
                grid {
                    columns(visible: false) {
                        column(label:"Column", width:"150px")
                        column(label:"Value")
                    }
                    rows {
                        scaffoldProps.each { p ->
                        if(p.name != "id") {
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