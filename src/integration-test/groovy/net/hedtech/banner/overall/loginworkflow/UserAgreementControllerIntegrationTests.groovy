/*******************************************************************************
 Copyright 2017-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.overall.loginworkflow

import grails.converters.JSON
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.general.utility.InformationTextUtility
import net.hedtech.banner.security.BannerGrantedAuthorityService
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.banner.web.SsbLoginURLRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Integration test cases for UserAgreementController.
 */
@Integration
@Rollback
class UserAgreementControllerIntegrationTests extends BaseIntegrationTestCase {
    private static final VIEW ='/userAgreement/policy'
    private static final String POLICY_PAGE_NAME = 'TERMSOFUSAGE'
    private static final String TERMS_OF_USAGE_LABEL = 'terms.of.usage'
    private static final String PATH_ENDS_WITH_CONTROLLER_NAME = "/ssb/home"
    private static final ACTION_DONE = 'true'
    private static final String BANNER_ID = 'HOF00720'
    private static final String SLASH = "/"
    def ssbLoginURLRequest
    def userAgreementService

    @Before
    public void setUp() {
        formContext = ['SELFSERVICE']
        controller = new UserAgreementController()
        controller.userAgreementService = userAgreementService
        controller.ssbLoginURLRequest = new SsbLoginURLRequest()
        super.setUp()
    }

    @After
    public void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    public void testIndex() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm

        controller.index()
        assertNotNull(renderMap)
        assertEquals(controller.modelAndView.viewName, VIEW)

        def infoText = InformationTextUtility.getMessage(POLICY_PAGE_NAME, TERMS_OF_USAGE_LABEL)
        assertEquals(controller.modelAndView.model.infoText, infoText)
    }

    @Test
    public void testIndexWithParam() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        def infoText = InformationTextUtility.getMessage(POLICY_PAGE_NAME, TERMS_OF_USAGE_LABEL)
        assertNotNull pidm

        controller.index()
        assertEquals(200, controller.response.status)
        assertNotNull(renderMap)
        assertEquals(controller.modelAndView.viewName, VIEW)
        assertEquals(controller.modelAndView.model.infoText, infoText)
    }


    @Test
    public void testAggrement() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm

        controller.agreement()
        def action = controller.session.getAttribute(UserAgreementFlow.USER_AGREEMENT_ACTION)
        assertEquals(action, ACTION_DONE)
    }


    @Test
    public void testAggrementWithPath() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm

        controller.session.setAttribute(PostLoginWorkflow.URI_ACCESSED, PATH_ENDS_WITH_CONTROLLER_NAME)
        def result = controller.agreement()
        assertEquals(200, controller.response.status)
        def action = controller.session.getAttribute(UserAgreementFlow.USER_AGREEMENT_ACTION)
        assertEquals(result.uri, PATH_ENDS_WITH_CONTROLLER_NAME)
        assertEquals(action, ACTION_DONE)
    }


    @Test
    public void testDoneWithNoPreURI_ACCESSED() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm

        testDone()
    }

    @Test
    public void testDoneWithPreURI_ACCESSED() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm

        controller.request.getSession().setAttribute(PostLoginWorkflow.URI_ACCESSED, 'TEST_PATH')
        testDone()
    }

    @Test
    public void testDoneWithRegisteredFlow() {
        loginForRegistration(BANNER_ID)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm


        Map<Integer, String> map = new HashMap<Integer, String>()
        map.put(1, 'userAgreement')


        PostLoginWorkflow postLoginWorkflow = new UserAgreementFlow()
        postLoginWorkflow.setRegisterFlowClass(map)
        PostLoginWorkflow.getListOfFlows().add(postLoginWorkflow)

        controller.request.getSession().setAttribute(PostLoginWorkflow.URI_ACCESSED, 'userAgreement')
        testDone()
    }

    private testDone() {
        String path = controller.request.getSession().getAttribute(PostLoginWorkflow.URI_ACCESSED)
        controller.done()

        def pathFromSession = controller.request.getSession().getAttribute(PostLoginWorkflow.URI_REDIRECTED)

        if (path == null) {
            assertEquals(pathFromSession, SLASH)
        } else {
            assertEquals(pathFromSession, checkPath(path))
        }
    }

    private Authentication loginForRegistration(String bannerId) {
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(bannerId, '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        return authentication
    }

    private String checkPath(String path) {
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
}
