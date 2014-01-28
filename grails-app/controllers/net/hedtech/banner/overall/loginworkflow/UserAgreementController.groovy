/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.overall.loginworkflow

import net.hedtech.banner.general.utility.InformationTextUtility
import net.hedtech.banner.loginworkflow.PostLoginWorkflow
import net.hedtech.banner.security.BannerGrantedAuthorityService

class UserAgreementController {

    def userAgreementService

    static defaultAction = "index"
    private static final ACTION_DONE = "true"
    private static final String SLASH = "/"
    private static final VIEW = "policy"
    private static final String POLICY_PAGE_NAME = 'TERMSOFUSAGE'
    private static final String TERMS_OF_USAGE_LABEL = 'terms.of.usage'
    def ssbLoginURLRequest

    def index() {
        def infoText = getTermsOfUseInfoText()
        def model = [infoText: infoText]
        log.info("rendering view")
        render view: VIEW, model: model
    }

    def agreement() {
        String pidm = BannerGrantedAuthorityService.getPidm()
        userAgreementService.updateUsageIndicator(pidm, UserAgreementFlow.TERMS_OF_USAGE_ANSWERED);
        request.getSession().setAttribute(UserAgreementFlow.USER_AGREEMENT_ACTION, ACTION_DONE)
        done();
    }

    def done() {
        String path = request.getSession().getAttribute(PostLoginWorkflow.URI_ACCESSED)
        if (path == null) {
            path = SLASH
        } else {
            path = checkPath(path)
        }
        request.getSession().setAttribute(PostLoginWorkflow.URI_REDIRECTED, path)
        redirect uri: path
    }

    protected String checkPath(String path) {
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(path)
        List<PostLoginWorkflow> listOfFlows = PostLoginWorkflow.getListOfFlows()
        for (PostLoginWorkflow flow : listOfFlows) {
            if (flow.getControllerName().equals(controllerName)) {
                path = SLASH
                return path
            }
        }
        return path
    }

    private String getTermsOfUseInfoText() {
        return InformationTextUtility.getMessage(POLICY_PAGE_NAME, TERMS_OF_USAGE_LABEL)
    }

}
