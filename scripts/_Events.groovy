final ZK_THEMES_DIR = "${basedir}/zk-themes/"

eventSetClasspath = { classLoader ->
    if (new File(ZK_THEMES_DIR).exists()) {
        println "Adding theme jars to class loader"
        def themeJars = resolveResources("file:${ZK_THEMES_DIR}*.jar")
        for (jar in themeJars) {
            classLoader.addURL(jar.URL)
        }
    }
}

eventCreateWarStart = { warLocation, stagingDir ->
    // Include ZK's themes in war. Issue 42
    if (new File(ZK_THEMES_DIR).exists()) {
        ant.copy(todir: new File(stagingDir, 'WEB-INF/lib')) {
            fileset(dir: ZK_THEMES_DIR, includes: '*.jar')
        }
    }
}
