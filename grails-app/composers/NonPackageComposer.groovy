import org.zkoss.zkgrails.*

class NonPackageComposer extends GrailsComposer {

    def btnAdd

    def onClick_btnAdd() {
        btnAdd.label = "clicked"
    }

    def afterCompose = { window ->
        // initialize components here
    }
}
