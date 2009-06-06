
eventSetClasspath = { classLoader ->
    if(new File("${basedir}/zk-themes/").exists()) {
        println "Adding theme jars to class loader"
        def themeJars = resolveResources("file:${basedir}/zk-themes/*.jar")
        for(jar in themeJars) {
            classLoader.addURL(jar.URL)
        }
    }
}

eventCreateWarStart = { warLocation, stagingDir ->
    def appVersion = metadata.'app.version'
    def appName = config.google.appengine.application ?: grailsAppName

    println "Generating ZK support appengine-web.xml file for application [$appName]"
    new File("$stagingDir/WEB-INF/appengine-web.xml").write """<?xml version=\"1.0\" encoding=\"utf-8\"?>
<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">
    <application>${appName}</application>
    <version>${appVersion}</version>
    <sessions-enabled>true</sessions-enabled>
    <static-files>
        <exclude path=\"/**.zul\"/>
        <exclude path=\"/**.zhtml\"/>
    </static-files>
    <resource-files>
        <include path=\"/**.zul\"/>
        <include path=\"/**.zhtml\"/>
    </resource-files>
</appengine-web-app>
"""
}
