/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
****************************************************************************** */
package net.hedtech.banner.overall.loginworkflow

import grails.util.Holders;
import org.springframework.context.ApplicationContext
import net.hedtech.banner.security.BannerGrantedAuthorityService

class SecurityQAFlow extends PostLoginWorkflow {

    def securityQAService
    private static final FORGET_PIN_INDICATOR = "N"

    public boolean isShowPage(request) {
        def session = request.getSession();
        String isDone = session.getAttribute(SecurityQAController.SECURITY_QA_ACTION)
        boolean displayPage = false
        if (isDone != SecurityQAController.ACTION_DONE) {
            initializeSecurityQAService()
            Map map = getUserDefinedPreference()
            def noOfQuestions = getNumberOfQuestions(map)
            if (getDisableForgetPinIndicator(map).equals(FORGET_PIN_INDICATOR) && noOfQuestions > 0 && !isUserAlreadyAnsweredSecurityQA(noOfQuestions)) {
                displayPage = true
            }
        }

        return displayPage
    }

    public String getControllerUri() {
        return "/ssb/securityQA"
    }

    public String getControllerName() {
        return "securityQA"
    }

    private void initializeSecurityQAService() {
        ApplicationContext ctx = (ApplicationContext) Holders.getGrailsApplication().getMainContext()
        securityQAService = (SecurityQAService) ctx.getBean("securityQAService")
    }

    private Map getUserDefinedPreference() {
        return securityQAService.getUserDefinedPreference()
    }

    private def getNumberOfQuestions(Map map) {
        return map?.GUBPPRF_NO_OF_QSTNS
    }

    private String getDisableForgetPinIndicator(Map map) {
        return map?.GUBPPRF_DISABLE_FORGET_PIN_IND
    }

    private boolean isUserAlreadyAnsweredSecurityQA(noOfQuestions) {
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        if (pidm && noOfQuestions > securityQAService.getNumberOfQuestionsAnswered(pidm)) {
            return false
        }
        return true
    }

}
