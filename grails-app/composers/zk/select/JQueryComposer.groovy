package zk.select

import org.zkoss.zk.grails.composer.GrailsComposer

class JQueryComposer extends GrailsComposer { def afterCompose = { root ->

    $('#button').on("click", { event ->

        alert($(delegate).text())

    });

}}
