eventSetClasspath = { classLoader ->

    //
    // support themes:
    // check if exists META-INF\maven\org.zkoss.theme in JAR file
    //
    def themeJars = resolveResources("file:${basedir}/lib/*.jar")
    for(lib in themeJars) {
        def themeDir = resolveResources("jar:${lib.getURL()}!/META-INF/maven/org.zkoss.theme")
        if(themeDir.size() > 0 && themeDir[0].exists()) {
            classLoader.addURL(lib.getURL())
        }
    }

    //
    // Issue #140 - Support sub-plugins for every plugin with 'zk' prefix.
    // Adding all jars from different plugins into the same class loader.
    //
    // To check which variables are available to use:
    //   println delegate.variables.each { k, v -> println k }
    //
    try {
        def subPluginDirs = delegate.variables.collect { k, v ->
            if(k.startsWith("zk")      &&
               k.endsWith("PluginDir") &&
               k != "zkPluginDir")
                return v
        }

        subPluginDirs.each { dir ->
            if(new File("${dir}/lib/").exists()) {
                // println "Adding jars from ${dir} to the class loader"
                def eeJars = resolveResources("${dir}/lib/*.jar")
                for(jar in eeJars) {
                    classLoader.addURL(jar.URL)
                }
            }
        }
    }catch(groovy.lang.MissingPropertyException e) {
        println "Sub-plugin's jars not added to the class loader properly."
    }
}

eventCreateWarStart = { warLocation, stagingDir ->

    ant.copy(todir:"$stagingDir/WEB-INF/grails-app/zul/") {
        fileset(dir:"$basedir/grails-app/zul")
    }

    def supportZkGae

    //
    // #144 - Support GAE as an optional grails.zk.gae on Config.groovy
    //
    if(config.grails.zk.gae != null) {
        supportZkGae = config.grails.zk.gae
    } else {
        supportZkGae = true // default to true to support backward compatibility
    }

    if(supportZkGae) {
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

}
