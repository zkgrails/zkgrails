@artifact.package@class @artifact.name@ extends GrailsComposer {

    def txtName
    def btnHello
    def lstResult
    
    def onClick_btnHello() {
        lstResult.clear()
        lstResult.append {
            listitem { listcell { label(value: "Hello, ${txtName.value} !") } }
        }
    }
}
