grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
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
        mavenCentral()
        mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://zkgrails.googlecode.com/svn/repo/"
        mavenRepo "http://mavensync.zkoss.org/maven2/"
    }
    dependencies {
        build ("net.java.dev.inflector:inflector:0.7.0")
        build ("com.google.code.maven-svn-wagon:maven-svn-wagon:1.4") {
            export = false
        }

        test ("com.h2database:h2:1.2.147")
    }

    plugins {
        // test ":selenium-rc:1.0.2"
        build(":tomcat:$grailsVersion",
              ":release:1.0.1",
              ":svn:1.0.2") {
            export = false
        }
        runtime(":resources:1.1.6") {
            export = false
        }
    }
}
