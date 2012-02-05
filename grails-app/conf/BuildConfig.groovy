grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsHome()
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenCentral()
        mavenLocal()
        // mavenRepo "http://snapshots.repository.codehaus.org"
        // mavenRepo "http://repository.codehaus.org"
        // mavenRepo "http://download.java.net/maven/2/"
        // mavenRepo "http://localhost:8081/artifactory/repo/"
        mavenRepo "http://zkgrails.googlecode.com/svn/repo/"
        mavenRepo "http://mavensync.zkoss.org/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        build ("net.java.dev.inflector:inflector:0.7.0")
        build ("com.google.code.maven-svn-wagon:maven-svn-wagon:1.4") {
            export = false
        }

        def zkVersion = "6.0.0.FL.20120201"

        runtime "org.zkoss.zk.grails:zk-grails-common:1.0.0.BUILD-SNAPSHOT"

        runtime "org.zkoss.zk:zk:${zkVersion}"
        runtime "org.zkoss.zk:zul:${zkVersion}"
        runtime "org.zkoss.zk:zhtml:${zkVersion}"
        runtime "org.zkoss.zk:zkplus:${zkVersion}"
        runtime "org.zkoss.zk:zkbind:${zkVersion}"
        runtime "org.zkoss.zkforge:ckez:3.5.2.0"

        test ("com.h2database:h2:1.2.147")
    }

    plugins {
        test ":selenium-rc:1.0.2"
        build(":tomcat:$grailsVersion",
              ":release:1.0.1",
              ":svn:1.0.2") {
            export = false
        }
    }
}
