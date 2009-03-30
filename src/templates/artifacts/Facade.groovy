@artifact.package@import org.zkoss.zkgrails.*

class @artifact.name@ {

    @artifact.domain@ selected

    List<@artifact.domain@> get@artifact.domain.plural@() {
        @artifact.domain@.list()
    }
}
