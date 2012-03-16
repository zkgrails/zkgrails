/*
 * Copyright 2011-2012 the original author or authors.
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
 * Gant script that creates a new ZUL page with an associated Grails composer
 *
 * @author Chanwit Kaewkasi
 *
 * @since 2.0
 */

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsResourceUtils

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target ('default': "Creates a new zul page") {
    depends(checkVersion, parseArguments)

    def type = "ZUL"
    promptForName(type: type)
    def name = argsMap["params"][0]

    //
    // #75 - Replaces "/" and "\" with "."
    //
    name = name.replace('/', '.').replace('\\', '.')

    //
    // #110 - Removes the last Composer if user accidentally inputted
    //
    if(name.endsWith("ViewModel"))
        name = name.substring(0, name.indexOf("ViewModel"))

    def suffix = "ViewModel"
    def artifactPath = "grails-app/viewmodels"
    createArtifact(name: name, suffix: suffix, type: suffix, path: artifactPath)

    def pkg = null
    def pos = name.lastIndexOf('.')
    if (pos != -1) {
        pkg = name[0..<pos]
        name = name[(pos + 1)..-1]
    }

    // Convert the package into a file path.

    def pkgPath = ''
    def zulPkgPath = ''
    if (pkg) {
        pkgPath = pkg.replace('.' as char, '/' as char)
        pkgPath += '/'

        zulPkgPath = pkgPath
    }
    //
    // #109 - Grails enforces use of package, we have to go along then
    // #132 - Ignore #109 just for .zul files, to conform Grails' convention
    //
    else {
        pkgPath = config.grails.project.groupId ? config.grails.project.groupId: grailsAppName
        pkgPath = pkgPath.replace('-' as char,'/' as char).replace('.' as char,'/' as char).toLowerCase() + "/"
    }

    def propName  = GrailsNameUtils.getPropertyNameRepresentation(name)
    def className = GrailsNameUtils.getClassNameRepresentation(name)
    def zulFile = "${basedir}/grails-app/zul/${zulPkgPath}${propName}.zul"

    className = "${pkgPath.replace('/', '.')}${className}ViewModel"
    propName  = "${propName}ViewModel"

    ant.copy(
        file:"${zkPluginDir}/src/templates/artifacts/template_vm.zul",
        tofile: zulFile,
        overwrite: true
    )
    ant.replace(
        file: zulFile,
        token: "@artifact.name@",
        value: propName
    )

    // Convert the given name into class name and property name
    // representations.
    className = GrailsNameUtils.getClassNameRepresentation(name)
    propertyName = GrailsNameUtils.getPropertyNameRepresentation(name)
    artifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${suffix}.groovy"

    ant.replace(
        file: "${artifactFile}",
        token: "@artifact.name.prop@",
        value: propName
    )
    createUnitTest(name: name, suffix: "ViewModel")
}