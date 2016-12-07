/*******************************************************************************
 Copyright 2014-2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.overall.loginworkflow

import groovy.sql.Sql
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SecurityQAFlowIntegrationTests extends BaseIntegrationTestCase {


    def securityQAFlow
    private String SSB_VALID_USERNAME = "HOSH00002"
    private String SSB_VALID_PASSWORD = "111111"

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        String username = SSB_VALID_USERNAME
        String password = SSB_VALID_PASSWORD
        login()
        modifiyForgetPINMethod()
    }


    @After
    public void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void testIsShowPage() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        request.getSession().setAttribute(SecurityQAController.SECURITY_QA_ACTION, false)
        def res = securityQAFlow.isShowPage(request)
        assertNotNull(res)
    }

    @Test
    void testIsShowPageTrue() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        request.getSession().setAttribute(SecurityQAController.SECURITY_QA_ACTION, true)
        def res = securityQAFlow.isShowPage(request)
        assertNotNull(res)
    }

    @Test
    void testControllerUri() {
        def res = securityQAFlow.getControllerUri()
        assertEquals("/ssb/securityQA", res)
    }

    @Test
    void testControllerName() {
        def res = securityQAFlow.getControllerName()
        assertEquals("securityQA", res)
    }

    private modifiyForgetPINMethod() {
        def sql
        try {
            sql = new Sql(sessionFactory.getCurrentSession().connection())
            def backup = sql.firstRow("""select GUBPPRF_DISABLE_FORGET_PIN_IND from GUBPPRF""")
            def forgetPIN = backup.get('GUBPPRF_DISABLE_FORGET_PIN_IND')
            if (forgetPIN != 'N')
                sql.executeUpdate("update GUBPPRF set GUBPPRF_DISABLE_FORGET_PIN_IND = 'N'")
        } finally {
            sql?.close()
        }
    }

}
