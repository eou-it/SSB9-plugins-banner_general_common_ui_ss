/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.plugin.location.'banner-general-validation-common' = "../banner_general_validation_common.git"
grails.plugin.location.'banner-general-person' = "../banner_general_person.git"
grails.plugin.location.'banner-general-common' = "../banner_general_common.git"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

    inherits("global") {
    }

    log "error"

    repositories {
        if (System.properties['PROXY_SERVER_NAME']) {
            mavenRepo "${System.properties['PROXY_SERVER_NAME']}"
        } else {
            grailsCentral()
            mavenCentral()
            mavenRepo "http://repository.jboss.org/maven2/"
            mavenRepo "https://code.lds.org/nexus/content/groups/main-repo"
        }
    }

    dependencies {
    }

    plugins {
    }
}
