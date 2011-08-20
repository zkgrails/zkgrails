import org.zkoss.zkgrails.*

class NonPackageComposer extends org.zkoss.zk.grails.GrailsComposer {

    def btnAdd

    def onClick_btnAdd() {
        btnAdd.label = "clicked"
    }

    def afterCompose = { window ->
        // initialize components here
    }
}
