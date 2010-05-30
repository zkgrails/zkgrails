final ZK_THEMES_DIR = "${basedir}/zk-themes/"

eventSetClasspath = { classLoader ->

    //
    // support themes
    //
    if(new File(ZK_THEMES_DIR).exists()) {
        println "Adding theme jars to class loader"
        def themeJars = resolveResources("file:${ZK_THEMES_DIR}*.jar")
        for(jar in themeJars) {
            classLoader.addURL(jar.URL)
        }
    }

    //
    // Support ZK-EE.
    // Adding all jars from different plugins into the same class loader.
    //
    // To check which variables are available to use:
    //   println delegate.variables.each { k, v -> println k }
    //
    try {
        if(metadata['plugins.zk-ee']) {
            if(new File("${zkEePluginDir}/lib/").exists()) {
                println "Adding ZK-EE jars to the class loader"
                def eeJars = resolveResources("${zkEePluginDir}/lib/*.jar")
                for(jar in eeJars) {
                    classLoader.addURL(jar.URL)
                }
            }
        }
    }catch(groovy.lang.MissingPropertyException e) {
        println "ZK-EE jars not added to the class loader."
    }
}

eventCreateWarStart = { warLocation, stagingDir ->
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

    // Include ZK's themes in war. Issue #42
    if (new File(ZK_THEMES_DIR).exists()) {
        ant.copy(todir: new File(stagingDir, 'WEB-INF/lib')) {
            fileset(dir: ZK_THEMES_DIR, includes: '*.jar')
        }
    }
}
