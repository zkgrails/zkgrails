includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(main: "The description of the script goes here!") {
    depends(checkVersion, parseArguments)

    def type = "Controller"
    promptForName(type: type)

    def name = argsMap["params"][0]
	createArtifact(name: name, suffix: type, type: type, path: "web-inf/")

    def viewsDir = "${basedir}/grails-app/views/${propertyName}"
    ant.mkdir(dir:viewsDir)
	event("CreatedFile", [viewsDir])

	// createUnitTest(name: name, suffix: type, superClass: "ControllerUnitTestCase")
}

setDefaultTarget(main)
