class MyTagLib {

    static namespace = "my"

    def logo = { attrs, body ->
        def src = z.resource(dir:'images', file: attrs.remove("src"))
        out << "<image src=\"${src}\"/>"
    }
}