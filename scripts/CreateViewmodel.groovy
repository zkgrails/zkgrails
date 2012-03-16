/**
* Gant script that creates a new ZKGrails ViewModel
*
* @author Chanwit
*
* @since 2.0.0-M2
*/

import grails.util.GrailsNameUtils

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target ('default': "Creates a new ViewModel") {
    depends(checkVersion, parseArguments)
    def TYPE = "ViewModel"
    promptForName(type: TYPE)

    def name = argsMap["params"][0]
    name = name.replace('/', '.').replace('\\', '.')

    if(name.endsWith(TYPE))
        name = name.substring(0, name.indexOf(TYPE))

    createArtifact(name: name, suffix: TYPE, type: TYPE, path: "grails-app/viewmodels")

    // check if input contains package
    def pkg = null
    def pos = name.lastIndexOf('.')
    if (pos != -1) {
        pkg = name[0..<pos]
        name = name[(pos + 1)..-1]
    }

    // Convert the package into a file path.
    def pkgPath = ''
    if (pkg) {
        pkgPath = pkg.replace('.' as char, '/' as char)
        // Future use of 'pkgPath' requires a trailing slash.
        pkgPath += '/'
    }

    def propName = GrailsNameUtils.getPropertyNameRepresentation(name)

    //
    // #109 - Grails enforces use of package, we have to go along then
    //
    def filename
    if(pkg)
        filename = pkgPath + GrailsNameUtils.getClassName(name, TYPE)
    else
        filename = (config.grails.project.groupId ?: grailsAppName).replace('-','/').toLowerCase() + "/" + GrailsNameUtils.getClassName(name, TYPE)

    // create a facade property for the composer
    ant.replace(
        file: "${basedir}/grails-app/viewmodels/${filename}.groovy",
        token: "@artifact.name.prop@",
        value: propName
    )
    createUnitTest(name: name, suffix: TYPE)
}