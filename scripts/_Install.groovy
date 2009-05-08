//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//
def targetFile = "${basedir}/web-app/WEB-INF/zk.xml"
def proceed = true
if(new File(targetFile).exists()) {
    proceed = false
    ant.input(message: "Overwrite the existing zk.xml?", addproperty: "overwrite.zk", validargs: "y,n")
    proceed = ant.antProject.properties["overwrite.zk"] == 'y'
}

if(proceed == true) {
    ant.copy(file:"${zkPluginDir}/scripts/zk.xml", todir:"${basedir}/web-app/WEB-INF/", overwrite: true)
}

// ant.mkdir(dir:"${basedir}/web-app/WEB-INF/")