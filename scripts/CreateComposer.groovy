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
 * Gant script that creates a new Grails controller
 *
 * @author Chanwit
 *
 * @since 0.7.1
 */

import grails.util.GrailsNameUtils

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target ('default': "Creates a new composer") {
    depends(checkVersion, parseArguments)

    def type = "Composer"
    promptForName(type: type)

    def name = argsMap["params"][0]
    def propName = GrailsNameUtils.getPropertyNameRepresentation(name)
    def filename = GrailsNameUtils.getClassName(name, type)

    createArtifact(name: name, suffix: type, type: type, path: "grails-app/composers")

    // create a facade property for the composer
    ant.replace(
        file: "${basedir}/grails-app/composers/${filename}.groovy",
        token: "@artifact.name.prop@",
        value: propName
    )

}
