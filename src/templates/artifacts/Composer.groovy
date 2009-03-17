@artifact.package@class @artifact.name@ extends GrailsComposer {

    def txtName
    def btnSearch
    def lstResult
    
    def onClick_btnSearch() {
        lstResult.clear()
        lstResult.append {
            listitem { listcell { label(value: "Search result here") } }
        }
    }
}
