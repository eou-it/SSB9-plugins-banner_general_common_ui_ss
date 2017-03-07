/*******************************************************************************
 Copyright 2014-2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.overall.loginworkflow

import grails.util.Holders
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SurveyFlowIntegrationTests extends BaseIntegrationTestCase {


    def surveyFlow

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        login()
    }


    @After
    public void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void testIsShowPage() {
        def map = [:]
        def oldMap = Holders.config.grails.plugin.springsecurity.interceptUrlMap
        map.put('/ssb/survey/', ['ROLE_API_ABOUT_BAN_DEFAULT_M'])
        Holders.config.grails.plugin.springsecurity.interceptUrlMap = map
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        request.getSession().setAttribute(SurveyController.SURVEY_ACTION, false)
        def res = surveyFlow.isShowPage(request)
        Holders.config.grails.plugin.springsecurity.interceptUrlMap = oldMap
        assertNotNull(res)
    }

    @Test
    void testIsShowPageTrue() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def map = [:]
        def oldMap = Holders.config.grails.plugin.springsecurity.interceptUrlMap
        map.put('/ssb/survey/', ['ROLE_API_ABOUT_BAN_DEFAULT_M'])
        Holders.config.grails.plugin.springsecurity.interceptUrlMap = map
        request.getSession().setAttribute(SecurityQAController.SECURITY_QA_ACTION, true)
        def res = surveyFlow.isShowPage(request)
        Holders.config.grails.plugin.springsecurity.interceptUrlMap = oldMap
        assertNotNull(res)
    }

    @Test
    void testControllerUri() {
        def res = surveyFlow.getControllerUri()
        assertEquals("/ssb/survey/survey", res)
    }

    @Test
    void testControllerName() {
        def res = surveyFlow.getControllerName()
        assertEquals("survey", res)
    }

}
