/*******************************************************************************
 Copyright 2014-2020 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package banner.general.common.ui.ss

import grails.plugins.*
import net.hedtech.banner.overall.loginworkflow.SecurityQAFlow
import net.hedtech.banner.overall.loginworkflow.SurveyFlow
import net.hedtech.banner.overall.loginworkflow.UserAgreementFlow
import net.hedtech.banner.web.SsbLoginURLRequest

class BannerGeneralCommonUiSsGrailsPlugin extends Plugin {
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.11 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def loadAfter = ["bannerGeneralCommon"]

    // TODO Fill in these fields
    def title = "Banner General Common Ui Ss" // Headline display name of the plugin
    def author = "Ellucian"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/banner-general-common-ui-ss"

    Closure doWithSpring() { {->
          ssbLoginURLRequest(SsbLoginURLRequest) {
        }

        userAgreementFlow(UserAgreementFlow) { bean ->
            sessionFactory = ref('sessionFactory')

            registerFlowClass = [
                    10: "userAgreementFlow"
            ]
        }

        securityQAFlow(SecurityQAFlow) {
            registerFlowClass = [
                    30: "securityQAFlow"
            ]
        }

        surveyFlow(SurveyFlow) {
            sessionFactory = ref('sessionFactory')
            registerFlowClass = [
                    50: "surveyFlow"
            ]
        }
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
