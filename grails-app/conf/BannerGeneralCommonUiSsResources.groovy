/** *****************************************************************************
 Copyright 2014-2016 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

import net.hedtech.banner.security.SelfServiceBannerAuthenticationProvider

modules = {
    if (SelfServiceBannerAuthenticationProvider.isSsbEnabled()) {

        'userAgreementCommon' {
            dependsOn "commonComponents"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'js/views/userAgreement/policy.js']
        }

        'userAgreementLTR' {
            dependsOn "bannerSelfService, i18n-core, userAgreementCommon,commonComponentsLTR"
            defaultBundle environment == "development" ? false : "userAgreementLTR"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/userAgreement/policy.css'], attrs: [media: 'screen, projection']
        }

        'userAgreementRTL' {
            dependsOn "bannerSelfServiceRTL, i18n-core, userAgreementCommon,commonComponentsRTL"
            defaultBundle environment == "development" ? false : "userAgreementRTL"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/userAgreement/policy-rtl.css'], attrs: [media: 'screen, projection']
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/userAgreement/policy-rtl-patch.css'], attrs: [media: 'screen, projection']
        }

        'securityQACommon' {
            dependsOn "commonComponents"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'js/views/securityQA/securityQA.js']
        }

        'securityQALTR' {
            dependsOn "bannerSelfService, i18n-core, securityQACommon,commonComponentsLTR"
            defaultBundle environment == "development" ? false : "securityQALTR"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/securityQA/securityQA.css'], attrs: [media: 'screen, projection']
        }

        'securityQARTL' {
            dependsOn "bannerSelfServiceRTL, i18n-core, securityQACommon,commonComponentsRTL"
            defaultBundle environment == "development" ? false : "securityQARTL"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/securityQA/securityQA-rtl.css'], attrs: [media: 'screen, projection']
        }

        'surveyCommon' {
            dependsOn "commonComponents"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'js/views/survey/survey.js']
        }

        'surveyLTR' {
            dependsOn "bannerSelfService, i18n-core, surveyCommon,commonComponentsLTR"
            defaultBundle environment == "development" ? false : "surveyLTR"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/survey/survey.css'], attrs: [media: 'screen, projection']
        }

        'surveyRTL' {
            dependsOn "bannerSelfServiceRTL, i18n-core, surveyCommon,commonComponentsRTL"
            defaultBundle environment == "development" ? false : "surveyRTL"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/survey/survey-rtl.css'], attrs: [media: 'screen, projection']
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/survey/survey-rtl-patch.css'], attrs: [media: 'screen, projection']
        }
    }
}
