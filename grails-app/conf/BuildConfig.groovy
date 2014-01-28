/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.plugin.location.'banner-core' = "../banner_core.git"
grails.plugin.location.'banner-general-validation-common' = "../banner_general_validation_common.git"
grails.plugin.location.'banner-general-person' = "../banner_general_person.git"
grails.plugin.location.'banner-general-common' = "../banner_general_common.git"

grails.project.dependency.resolution = {

    inherits("global") {
    }

    log "warn"

    repositories {
        if (System.properties['PROXY_SERVER_NAME']) {
            mavenRepo "${System.properties['PROXY_SERVER_NAME']}"
        } else {
            grailsPlugins()
            grailsHome()
            grailsCentral()
            mavenCentral()
            mavenRepo "http://repository.jboss.org/maven2/"
            mavenRepo "http://repository.codehaus.org"
        }
    }

    dependencies {
    }

    plugins {
        compile ":hibernate:$grailsVersion"
        compile ":tomcat:$grailsVersion"
        test ':code-coverage:1.2.5'
    }
}
