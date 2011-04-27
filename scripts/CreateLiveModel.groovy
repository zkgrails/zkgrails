/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Gant script that creates a new ZKGrails UI-Model
 *
 * @author Chanwit
 *
 * @since 1.1-M1
 */

import grails.util.GrailsNameUtils

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target ('default': "Creates a new live model") {
    depends(checkVersion, parseArguments)

    def type = "LiveModel"
    promptForName(type: type)

    def name = argsMap["params"][0]

    //
    // #75 - Replaces "/" and "\" with "."
    //
    name = name.replace('/', '.').replace('\\', '.')

    //
    // #110 - Removes the last Composer if user accidentally inputted
    //
    if(name.endsWith("LiveModel"))
        name = name.substring(0, name.indexOf("LiveModel"))

    createArtifact(name: name, suffix: type, type: type, path: "grails-app/livemodels")

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
    def filename

    //
    // #109 - Grails enforces use of package, we have to go along then
    //
    if(pkg)
        filename = pkgPath + GrailsNameUtils.getClassName(name, type)
    else
        filename = (config.grails.project.groupId ?: grailsAppName).replace('-','/').toLowerCase() + "/" + GrailsNameUtils.getClassName(name, type)

    // create a domain property for the composer
    ant.replace(
        file: "${basedir}/grails-app/livemodels/${filename}.groovy",
        token: "@artifact.domain@",
        value: (GrailsNameUtils.getClassName(name, ""))
    )
}
