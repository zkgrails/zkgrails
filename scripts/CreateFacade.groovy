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
 * @since 0.7.2
 */

import static org.jvnet.inflector.Noun.pluralOf
import static java.util.Locale.*;
import grails.util.GrailsNameUtils

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target ('default': "Creates a new model facade") {
    depends(checkVersion, parseArguments)

    def type = "Facade"
    promptForName(type: type)

    def name = argsMap["params"][0]
    def domainToFacade = argsMap["params"][1]
    // println "0: " + argsMap["params"][0]
    // println "1: " + argsMap["params"][1]
    if(domainToFacade == null) {
        domainToFacade = name
        println "Domain class name is not specified as 2nd arg, use 1st arg instead."
    }
    def domainName = GrailsNameUtils.getClassName(domainToFacade, null)
    // println "Domain name : $domainName"
    def filename = GrailsNameUtils.getClassName(name, type)

	createArtifact(name: name, suffix: type, type: type, path: "grails-app/facade")

    ant.replace(
        file: "${basedir}/grails-app/facade/${filename}.groovy",
        token: "@artifact.domain@",
        value: domainName
    )

    ant.replace(
        file: "${basedir}/grails-app/facade/${filename}.groovy",
        token: "@artifact.domain.plural@",
        value: pluralOf(domainName, ENGLISH)
    )

}
