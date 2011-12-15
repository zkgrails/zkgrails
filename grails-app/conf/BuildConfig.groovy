grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    def zkVersion = "5.0.9"
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
        mavenCentral()
        // mavenLocal()
        // mavenRepo "http://snapshots.repository.codehaus.org"
        // mavenRepo "http://repository.codehaus.org"
        // mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://localhost:8081/artifactory/repo/"
        mavenRepo "http://zkgrails.googlecode.com/svn/repo/"
        mavenRepo "http://mavensync.zkoss.org/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        build ("net.java.dev.inflector:inflector:0.7.0")
        build ("com.google.code.maven-svn-wagon:maven-svn-wagon:1.4") {
            export = false
        }

        runtime "org.zkoss.zk.grails:zk:${zkVersion}"
        runtime "org.zkoss.zk.grails:zul:${zkVersion}"
        runtime "org.zkoss.zk.grails:zkplus:${zkVersion}"
        runtime "org.zkoss.zk.grails:zhtml:${zkVersion}"
        runtime ("org.zkoss.zk.grails:zweb:${zkVersion}") {
            excludes("javax.servlet:servlet-api:2.4")
        }

        runtime "org.zkoss.zkforge:ckez:3.5.2.0"

        test ("com.h2database:h2:1.2.147")
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:1.0.0",
              ":svn:1.0.1") {
            export = false
        }
        runtime(":resources:1.1.5") {
            export = false
        }
    }
}
