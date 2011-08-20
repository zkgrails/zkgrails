@artifact.package@import org.zkoss.zk.grails.*

class @artifact.name@ extends GrailsComet {

    static trigger = [startDelay: 0L, every: 1000L]

    def beforeExecute = { desktop, page ->
    }

    def execute = { desktop, page ->
        //
        // 'delegate' is the composer this comet belongs to.
        // So, you can directly use components (and properties)
        // defined in the composer.
        //
    }

    def afterExecute = { desktop, page ->
    }
}
