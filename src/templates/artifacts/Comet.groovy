@artifact.package@import org.zkoss.zkgrails.*

class @artifact.name@ extends GrailsComet {

    static trigger = [startDelay: 0L, delay: 1000L]

    def execute = { desktop, page ->
        // 'delegate' is the composer this comet belongs to.
        // So, you can directly use component defined in the composer.
    }
}
