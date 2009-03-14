import org.codehaus.groovy.runtime.InvokerHelper
import org.zkoss.zhtml.A
import org.zkoss.zhtml.Abbr
import org.zkoss.zhtml.Acronym
import org.zkoss.zhtml.Address
import org.zkoss.zhtml.Area
import org.zkoss.zhtml.B
import org.zkoss.zhtml.Base
import org.zkoss.zhtml.Big
import org.zkoss.zhtml.Blockquote
import org.zkoss.zhtml.Body
import org.zkoss.zhtml.Br
import org.zkoss.zhtml.Button
import org.zkoss.zhtml.Caption
import org.zkoss.zhtml.Cite
import org.zkoss.zhtml.Code
import org.zkoss.zhtml.Colgroup
import org.zkoss.zhtml.Dd
import org.zkoss.zhtml.Del
import org.zkoss.zhtml.Dfn
import org.zkoss.zhtml.Dir
import org.zkoss.zhtml.Dl
import org.zkoss.zhtml.Dt
import org.zkoss.zhtml.Em
import org.zkoss.zhtml.Embed
import org.zkoss.zhtml.Fieldset
import org.zkoss.zhtml.Font
import org.zkoss.zhtml.Form
import org.zkoss.zhtml.H1
import org.zkoss.zhtml.H2
import org.zkoss.zhtml.H3
import org.zkoss.zhtml.H4
import org.zkoss.zhtml.Head
import org.zkoss.zhtml.Hr
import org.zkoss.zhtml.Html
import org.zkoss.zhtml.I
import org.zkoss.zhtml.Iframe
import org.zkoss.zhtml.Img
import org.zkoss.zhtml.Input
import org.zkoss.zhtml.Ins
import org.zkoss.zhtml.Isindex
import org.zkoss.zhtml.Kbd
import org.zkoss.zhtml.Legend
import org.zkoss.zhtml.Li
import org.zkoss.zhtml.Link
import org.zkoss.zhtml.Menu
import org.zkoss.zhtml.Meta
import org.zkoss.zhtml.Nobr
import org.zkoss.zhtml.Ol
import org.zkoss.zhtml.Optgroup
import org.zkoss.zhtml.Option
import org.zkoss.zhtml.P
import org.zkoss.zhtml.Pre
import org.zkoss.zhtml.Q
import org.zkoss.zhtml.S
import org.zkoss.zhtml.Samp
import org.zkoss.zhtml.Script
import org.zkoss.zhtml.Select
import org.zkoss.zhtml.Small
import org.zkoss.zhtml.Span
import org.zkoss.zhtml.Strong
import org.zkoss.zhtml.Style
import org.zkoss.zhtml.Sub
import org.zkoss.zhtml.Sup
import org.zkoss.zhtml.Table
import org.zkoss.zhtml.Tbody
import org.zkoss.zhtml.Td
import org.zkoss.zhtml.Text
import org.zkoss.zhtml.Textarea
import org.zkoss.zhtml.Tfoot
import org.zkoss.zhtml.Th
import org.zkoss.zhtml.Thead
import org.zkoss.zhtml.Title
import org.zkoss.zhtml.Tr
import org.zkoss.zhtml.Tt
import org.zkoss.zhtml.Ul
import org.zkoss.zhtml.Var
import org.zkoss.zk.ui.event.CheckEvent
import org.zkoss.zk.ui.event.CreateEvent
import org.zkoss.zk.ui.event.DropEvent
import org.zkoss.zk.ui.event.ErrorEvent
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zk.ui.event.KeyEvent
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zk.ui.event.MoveEvent
import org.zkoss.zk.ui.event.OpenEvent
import org.zkoss.zk.ui.event.ScrollEvent
import org.zkoss.zk.ui.event.SelectEvent
import org.zkoss.zk.ui.event.SelectionEvent
import org.zkoss.zk.ui.event.SizeEvent
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zk.ui.event.ZIndexEvent
import org.zkoss.zkex.zul.Borderlayout
import org.zkoss.zkex.zul.Center
import org.zkoss.zkex.zul.Columnchildren
import org.zkoss.zkex.zul.Columnlayout
import org.zkoss.zkex.zul.East
import org.zkoss.zkex.zul.Fisheye
import org.zkoss.zkex.zul.Fisheyebar
import org.zkoss.zkex.zul.North
import org.zkoss.zkmax.event.PortalMoveEvent
import org.zkoss.zkmax.zul.Portalchildren
import org.zkoss.zkmax.zul.Portallayout
import org.zkoss.zul.Audio
import org.zkoss.zul.Auxhead
import org.zkoss.zul.Auxheader
import org.zkoss.zul.Bandbox
import org.zkoss.zul.Bandpopup
import org.zkoss.zul.Box
import org.zkoss.zul.Calendar
import org.zkoss.zul.Captcha
import org.zkoss.zul.Chart
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Column
import org.zkoss.zul.Columns
import org.zkoss.zul.Combobox
import org.zkoss.zul.Comboitem
import org.zkoss.zul.Datebox
import org.zkoss.zul.Detail
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Flash
import org.zkoss.zul.Foot
import org.zkoss.zul.Footer
import org.zkoss.zul.Grid
import org.zkoss.zul.Group
import org.zkoss.zul.Groupbox
import org.zkoss.zul.Groupfoot
import org.zkoss.zul.Hbox
import org.zkoss.zul.Image
import org.zkoss.zul.Imagemap
import org.zkoss.zul.Include
import org.zkoss.zul.Intbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listcell
import org.zkoss.zul.Listfoot
import org.zkoss.zul.Listfooter
import org.zkoss.zul.Listgroup
import org.zkoss.zul.Listgroupfoot
import org.zkoss.zul.Listhead
import org.zkoss.zul.Listheader
import org.zkoss.zul.Listitem
import org.zkoss.zul.Longbox
import org.zkoss.zul.Menubar
import org.zkoss.zul.Menuitem
import org.zkoss.zul.Menupopup
import org.zkoss.zul.Menuseparator
import org.zkoss.zul.Paging
import org.zkoss.zul.Panel
import org.zkoss.zul.Panelchildren
import org.zkoss.zul.Popup
import org.zkoss.zul.Progressmeter
import org.zkoss.zul.Radio
import org.zkoss.zul.Radiogroup
import org.zkoss.zul.Row
import org.zkoss.zul.Rows
import org.zkoss.zul.Slider
import org.zkoss.zul.Space
import org.zkoss.zul.Textbox
import org.zkoss.zul.event.ColSizeEvent
import org.zkoss.zul.event.PageSizeEvent
import org.zkoss.zul.event.PagingEvent
import org.zkoss.zul.Div
import org.zkoss.zul.Label

class ZkBuilder {

  private static java.util.Map ZKNODES = [audio: Audio, auxhead: Auxhead, auxheader: Auxheader, bandbox: Bandbox,
          bandpopup: Bandpopup, calendar: Calendar, borderlayout: Borderlayout, box: Box, button: Button,
          captcha: Captcha, caption: Caption, center: Center, chart: Chart, checkbox: Checkbox, column: Column,
          columnchildren: Columnchildren, columnlayout: Columnlayout, columns: Columns, combobox: Combobox,
          comboitem: Comboitem, datebox: Datebox, detail: Detail, doublebox: Doublebox, div: Div, east: East,
          fisheyebar: Fisheyebar, fisheye: Fisheye, flash: Flash, footer: Footer, foot: Foot, grid: Grid,
          group: Group, groupbox: Groupbox, groupfoot: Groupfoot, hbox: Hbox, html: Html, iframe: Iframe,
          image: Image, imagemap: Imagemap, include: Include, intbox: Intbox, label: Label, listbox: Listbox,
          listcell: Listcell, listfoot: Listfoot, listfooter: Listfooter, listgroup: Listgroup,
          listgroupfoot: Listgroupfoot, listhead: Listhead, listheader: Listheader, listitem: Listitem,
          longbox: Longbox, menu: Menu, menubar: Menubar, menuitem: Menuitem, menupopup: Menupopup,
          menuseparator: Menuseparator, north: North, paging: Paging, panel: Panel, panelchildren: Panelchildren,
          popup: Popup, portallayout: Portallayout, portalchildren: Portalchildren, progressmeter: Progressmeter,
          radio: Radio, radiogroup: Radiogroup, row: Row, rows: Rows, slider: Slider, space: Space,
          textbox: Textbox,
          
          checkEvent: CheckEvent, colSizeEvent: ColSizeEvent, createEvent: CreateEvent, dropEvent: DropEvent,
          errorEvent: ErrorEvent, event: Event, inputEvent: InputEvent, keyEvent: KeyEvent, mouseEvent: MouseEvent,
          moveEvent: MoveEvent, openEvent: OpenEvent, pageSizeEvent: PageSizeEvent, pagingEvent: PagingEvent,
          portalMoveEvent: PortalMoveEvent, scrollEvent: ScrollEvent, selectEvent: SelectEvent,
          selectionEvent: SelectionEvent, sizeEvent: SizeEvent, uploadEvent: UploadEvent, zIndexEvent: ZIndexEvent,
          a: A, abbr: Abbr, acronym: Acronym, address: Address, area: Area, b: B, base: Base,
          big: Big, blockquote: Blockquote, body: Body, br: Br, htmlButton: Button, htmlCaption: Caption, cite: Cite,
          code: Code, collection: Collection, colgroup: Colgroup, dd: Dd, del: Del, dfn: Dfn, dir: Dir, htmlDiv: org.zkoss.zhtml.Div,
          dl: Dl, dt: Dt, em: Em, embed: Embed, fieldset: Fieldset, font: Font, form: Form, h1: H1, h2: H2, h3: H3,
          h4: H4, head: Head, hr: Hr, i: I, htmlIframe: Iframe, img: Img, input: Input, ins: Ins,
          isindex: Isindex, kbd: Kbd, htmlLabel: org.zkoss.zhtml.Label, legend: Legend, li: Li, link: Link, map: org.zkoss.zhtml.Map, htmlMenu: Menu,
          meta: Meta, nobr: Nobr, object: org.zkoss.zhtml.Object, ol: Ol, optgroup: Optgroup, option: Option, p: P, pre: Pre, q: Q,
          s: S, samp: Samp, script: Script, select: Select, small: Small, span: Span, strong: Strong,
          style: Style, sub: Sub, sup: Sup, table: Table, tbody: Tbody, td: Td, text: Text, textarea: Textarea,
          tfoot: Tfoot, th: Th, thead: Thead, title: Title, tr: Tr, tt: Tt, ul: Ul, var: Var]

  def parent
  java.util.Map idComponents = [:]

  def methodMissing(String name, args) {
    if (ZKNODES[name]) {
      List argList = InvokerHelper.asList(args)
      def zkObject = null
      Closure closure = null
      switch (argList.size()) {
        case 0:
          zkObject = newInstance(name)
          break;
        case 1:
          def arg = argList[0]
          if (arg instanceof Closure) {
            closure = arg
            zkObject = newInstance(name)
          } else if (arg instanceof String) {
            zkObject = newInstance(name, [:], arg)
          } else if (arg instanceof Map) {
            zkObject = newInstance(name, arg)
          } else {
            throw new MissingMethodException(name.toString(), getClass(), argList.toArray(), false);
          }
          break;
        case 2:
          def arg = argList[0]
          def arg2 = argList[1]
          if (arg2 instanceof String && arg instanceof Map) {
            zkObject = newInstance(name, arg, arg2)
          } else if (arg instanceof String && arg2 instanceof Closure) {
            zkObject = newInstance(name, [:], arg)
            closure = arg2
          } else if (arg instanceof Map && arg2 instanceof Closure) {
            zkObject = newInstance(name, arg)
            closure = arg2
          } else {
            throw new MissingMethodException(name.toString(), getClass(), argList.toArray(), false);
          }
          break;
        case 3:
          zkObject = newInstance(name, argList[0], argList[1])
          closure = argList[2]
          break;
        default:
          throw new MissingMethodException(name.toString(), getClass(), list.toArray(), false);
      }

      if (closure) {
        def oldParent = parent
        parent = zkObject
        closure.delegate = this
        closure(zkObject)
        parent = oldParent
      }
      return zkObject
    }
    else if (!createEventListener(parent, name, args)) {
      throw new MissingMethodException(name, getClass(), args, false)
    }
  }

  private def createEventListener(zkObject, String name, args) {
    def listener = InvokerHelper.asList(args)[0]
    if(listener instanceof Closure) {
      Closure cls = listener
      ZkBuilder zkBuilder = new ZkBuilder()
      zkBuilder.idComponents = idComponents
      cls.delegate = zkBuilder
    }
    if (name =~ /on[A-Z]\w+/) {
      zkObject.addEventListener(name, listener as EventListener)
      return true
    }
    return false
  }

  def propertyMissing(String name) {
    if (idComponents.containsKey(name)) {
      return idComponents[name]
    }
    throw new MissingPropertyException(name, getClass())
  }

  Object newInstance(String name, java.util.Map args = [:], String id = null) {
    def zkObject = ZKNODES[name].newInstance()

    attachId(id, zkObject)

    addAttributeEvents(zkObject, args)

    args.each {key, value ->
      zkObject[key] = value
    }
    if (parent) {
      zkObject.parent = parent
    }
    return zkObject
  }

  private def attachId(String id, zkObject) {
    if (id) {
      Class clazz = ZKNODES[id]
      if (clazz) {
        throw new IllegalArgumentException("Cannot use $id as an ID.  It clashes with $clazz")
      }
      zkObject.id = id
      idComponents[id] = zkObject
    }
  }

  void addAttributeEvents(zkObject, java.util.Map args) {
    args.each {key, value ->
      if (createEventListener(zkObject, key, value)) {
        args.remove(key)
      }
    }
  }
}
