package test

import org.zkoss.zk.grails.*
import org.zkoss.zk.grails.composer.GrailsComposer;

class IndexComposer extends GrailsComposer {
	
    def btnAdd
    def lstToDo
    def txtToDo
    
    def onClick_btnAdd() {
        lstToDo.append {
            listitem {
                listcell {
                    checkbox(onCheck: listitemHandler, label: txtToDo.value)
                }
            }
        }
        txtToDo.value = 'done'
    }

    def listitemHandler = { e ->
        def src = e.target
        if(src.checked == true) {
            src.sclass = 'strike'
        } else {
            src.sclass = ''
        }
    }	

    def afterCompose = { window ->
        // initialize components here
    }
}
