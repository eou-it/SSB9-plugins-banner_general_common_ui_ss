/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.overall.loginworkflow

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import net.hedtech.banner.security.BannerGrantedAuthorityService
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Integration test cases for UserAgreementController.
 */
class UserAgreementFlowIntegrationTests extends BaseIntegrationTestCase {
    def UserAgreementFlow
    public static final USER_AGREEMENT_ACTION = "useragreementdone"
    public static final ACTION_DONE = "true"
    public static final CONTROLLER_URI = "/ssb/userAgreement"
    public static final CONTROLLER_NAME = "userAgreement"
    public String TEST_BANNER_ID = "HOF00720"

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }

    @After
    public void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testIsShowPage() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def oldUserAgreementAction = request.getSession().getAttribute(USER_AGREEMENT_ACTION)
        def res = UserAgreementFlow.isShowPage(request)
        request.getSession().setAttribute(USER_AGREEMENT_ACTION, oldUserAgreementAction)
        assertNotNull(res)
    }

    @Test
    void testIsShowPageFalse() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def oldUserAgreementAction = request.getSession().getAttribute(USER_AGREEMENT_ACTION)
        request.getSession().setAttribute(USER_AGREEMENT_ACTION, ACTION_DONE)
        def res = UserAgreementFlow.isShowPage(request)
        request.getSession().setAttribute(USER_AGREEMENT_ACTION, oldUserAgreementAction)
        assertNotNull(res)
    }

    @Test
    void testControllerUri() {
        def res = UserAgreementFlow.getControllerUri()
        assertEquals(CONTROLLER_URI, res)
    }

    @Test
    void testControllerName() {
        def res = UserAgreementFlow.getControllerName()
        assertEquals(CONTROLLER_NAME, res)
    }

    @Test
    void testDisplayStatusTrue() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def oldUserAgreementAction = request.getSession().getAttribute(USER_AGREEMENT_ACTION)
        String ind = initialDisplayStatus()
        changeDisplayStatus('Y')
        def res = UserAgreementFlow.isShowPage(request)
        changeDisplayStatus(ind)
        request.getSession().setAttribute(USER_AGREEMENT_ACTION, oldUserAgreementAction)
        assertNotNull(res)
    }

    @Test
    void testUsageIndicator() {
        loginForRegistration(TEST_BANNER_ID)
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def oldUserAgreementAction = request.getSession().getAttribute(USER_AGREEMENT_ACTION)
        String ind = initialDisplayStatus()
        changeDisplayStatus('Y')
        def res = UserAgreementFlow.isShowPage(request)
        changeDisplayStatus(ind)
        request.getSession().setAttribute(USER_AGREEMENT_ACTION, oldUserAgreementAction)
        assertNotNull(res)
    }

    private initialDisplayStatus() {
        def sql = new Sql(sessionFactory.getCurrentSession().connection())
        GroovyRowResult row = sql.firstRow("""select TWGBWRUL_DISP_USAGE_IND from TWGBWRUL""")
        return row?.TWGBWRUL_DISP_USAGE_IND
    }

    private changeDisplayStatus(indicator) {
        def sql
        try {
            sql = new Sql(sessionFactory.getCurrentSession().connection())
            sql.executeUpdate("update TWGBWRUL set TWGBWRUL_DISP_USAGE_IND = ? ", [indicator])
        } finally {
            sql?.close() // note that the test will close the connection, since it's our current session's connection
        }
    }

    private Authentication loginForRegistration(String bannerId) {
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(bannerId, '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        return authentication

    }


}
