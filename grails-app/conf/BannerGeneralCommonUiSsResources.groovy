/** *****************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
****************************************************************************** */

import net.hedtech.banner.security.SelfServiceBannerAuthenticationProvider

modules = {
    if (SelfServiceBannerAuthenticationProvider.isSsbEnabled()) {

        'userAgreement' {
            dependsOn "bannerSelfService, i18n-core"
            defaultBundle environment == "development" ? false : "userAgreement"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/userAgreement/policy.css'], attrs: [media: 'screen, projection']
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'js/views/userAgreement/policy.js']
        }

        'userAgreementRTL' {
            dependsOn "bannerSelfServiceRTL, i18n-core, userAgreement"
            defaultBundle environment == "development" ? false : "userAgreementRTL"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/userAgreement/policy-rtl.css'], attrs: [media: 'screen, projection']
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/userAgreement/policy-rtl-patch.css'], attrs: [media: 'screen, projection']
        }

        'securityQA' {
            dependsOn "bannerSelfService, i18n-core"
            defaultBundle environment == "development" ? false : "securityQA"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/securityQA/securityQA.css'], attrs: [media: 'screen, projection']
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'js/views/securityQA/securityQA.js']
        }

        'securityQARTL' {
            dependsOn "bannerSelfServiceRTL, i18n-core, securityQA"
            defaultBundle environment == "development" ? false : "securityQARTL"
            resource url: [plugin: 'banner-general-common-ui-ss', file: 'css/views/securityQA/securityQA-rtl.css'], attrs: [media: 'screen, projection']
        }
    }
}
