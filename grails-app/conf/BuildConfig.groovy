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
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://203.158.7.11/artifactory/repo/"
        mavenRepo "http://zkgrails.googlecode.com/svn/repo/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        
        def zkVersion = "5.0.8.FL.20110818"

        build ("net.java.dev.inflector:inflector:0.7.0")

        compile ("org.zkoss.zk.grails:zk:${zkVersion}")
        compile ("org.zkoss.zk.grails:zul:${zkVersion}")
        compile ("org.zkoss.zk:zhtml:${zkVersion}")       { transitive = false }
        compile ("org.zkoss.zk:zkplus:${zkVersion}")      { transitive = false }
        compile ("org.zkoss.common:zcommon:${zkVersion}") { transitive = false }
        compile ("org.zkoss.common:zweb:${zkVersion}")    { transitive = false }
        compile ("org.zkoss.zkforge.el:zcommons-el:1.1.0")
        compile ("org.zkoss.zkforge:ckez:3.5.2.0")

        runtime ("org.zkoss.zk.grails:zk:${zkVersion}")
        runtime ("org.zkoss.zk.grails:zul:${zkVersion}")
        runtime ("org.zkoss.zk:zhtml:${zkVersion}")       { transitive = false }
        runtime ("org.zkoss.zk:zkplus:${zkVersion}")      { transitive = false }
        runtime ("org.zkoss.common:zcommon:${zkVersion}") { transitive = false }
        runtime ("org.zkoss.common:zweb:${zkVersion}")    { transitive = false }
        runtime ("org.zkoss.zkforge.el:zcommons-el:1.1.0")
        runtime ("org.zkoss.zkforge:ckez:3.5.2.0")

        runtime ("org.apache.poi:poi:3.2-FINAL")
        runtime ("jfree:jcommon:1.0.15")
        runtime ("jfree:jfreechart:1.0.13") { transitive = false }

        runtime ("jasperreports:jasperreports:3.5.3")
        runtime ("com.jhlabs:filters:2.0.235")

        runtime ("bsh:bsh:2.0b1")
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:1.0.0.RC1") {
            export = false
        }
    }
}
