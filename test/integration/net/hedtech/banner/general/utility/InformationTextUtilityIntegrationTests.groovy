/*********************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
**********************************************************************************/

package net.hedtech.banner.general.utility

import grails.util.Holders
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.context.SecurityContextHolder

class InformationTextUtilityIntegrationTests extends BaseIntegrationTestCase {

    def selfServiceBannerAuthenticationProvider
    private static final String PAGE_NAME = "TESTPAGE"
    private static final String PERSONA_STUDENT = "STUDENT"
    private static final String PERSONA_WEBUSER = "WEBUSER"
    private static final String PERSONA_DEFAULT = "DEFAULT"
    private static final String RECORD_BASELINE = "B"
    private static final String RECORD_LOCAL = "L"

    protected void setUp() {
        if (!isSsbEnabled()) return
        formContext = ['GUAGMNU'] // Since we are not testing a controller, we need to explicitly set this
        super.setUp()
    }


    protected void tearDown() {
        if (!isSsbEnabled()) return
        super.tearDown()
    }

    void testSingleValueKeyWithBaseline() {
        if (!isSsbEnabled()) return
        createBaselineWithSingleValueKey()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Baseline text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    void testMultipleValuesKeyWithBaseline() {
        if (!isSsbEnabled()) return
        createBaselineTestDataWithNotNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String value1 = "Baseline text no 0\nBaseline text no 1\nBaseline text no 2\nBaseline text no 3"
        String value2 = "Baseline second text no 0\nBaseline second text no 1\nBaseline second text no 2\nBaseline second text no 3"
        assertEquals(value1, informationText.key1)
        assertEquals(value2, informationText.key2)
        logout()
    }

    void testSingleKeyWithoutValue() {
        if (!isSsbEnabled()) return
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "key1"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    void testSingleValueKeyWithLocal() {
        if (!isSsbEnabled()) return
        createLocalWithSingleValueKey()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    void testMultipleValuesKeyWithLocalNullDate() {
        if (!isSsbEnabled()) return
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String expectedText = ""
        assertEquals(expectedText, informationText.key1)
        assertEquals(expectedText, informationText.key2)
        logout()
    }

    void testMultipleValuesKeyWithLocalNotNullDate() {
        if (!isSsbEnabled()) return
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithNotNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String value1 = "Local text no 0\nLocal text no 1\nLocal text no 2\nLocal text no 3"
        String value2 = "Local second text no 0\nLocal second text no 1\nLocal second text no 2\nLocal second text no 3"
        assertEquals(value1, informationText.key1)
        assertEquals(value2, informationText.key2)
        logout()
    }

    void testMultipleValuesKeyWithLocalSingleNullDate() {
        if (!isSsbEnabled()) return
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithSingleNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Local text no 0\nLocal text no 1\nLocal text no 2"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    void testLocalWithFutureStartDate() {
        if (!isSsbEnabled()) return
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithFutureStartDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Baseline text no 0\nBaseline text no 1\nBaseline text no 2\nBaseline text no 3"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }


    void testAnonymousUserSingleValue() {
        if (!isSsbEnabled()) return
        setAuthentication()
        createSingleLocalTestDataForWebUser();
        logout()
        setAnonymousAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    void testAnonymousUserMultipleValues() {
        if (!isSsbEnabled()) return
        setAuthentication()
        createMultipleLocalTestDataForWebUser();
        logout()
        setAnonymousAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        println informationText
        String expectedText = "Local text no 0\nLocal text no 1"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    void testDefaultPersonaSingleValue() {
        if (!isSsbEnabled()) return
        setAuthentication()
        createSingleDefaultLocalTestDataForUser();
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)

        createLocalWithSingleValueKey()
        informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)

        logout()
    }

    void testDefaultPersonaMultipleValue() {
        if (!isSsbEnabled()) return
        setAuthentication()
        createMultipleDefaultLocalTestDataForUser();
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "DEFAULT - Local text no 0\n" +
                "DEFAULT - Local text no 1"
        GroovyTestCase.assertEquals(expectedText, informationText)

        createLocalTestDataWithNotNullDate()
        informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        expectedText = "Local text no 0\nLocal text no 1\nLocal text no 2\nLocal text no 3"
        GroovyTestCase.assertEquals(expectedText, informationText)

        logout()
    }

    void setAnonymousAuthentication() {
        List roles = new ArrayList();
        GrantedAuthority grantedAuthority = new GrantedAuthorityImpl("ROLE_ANONYMOUS");
        roles.add(grantedAuthority);
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("anonymousUser", "anonymousUser", roles);
        SecurityContextHolder.getContext().setAuthentication(auth)
    }

    private def newValidForCreateInformationText() {
        def informationText = new InformationText(
                pageName: i_success_pageName,
                label: i_success_label,
                textType: i_success_textType,
                sequenceNumber: i_success_sequenceNumber,
                persona: i_success_persona,
                startDate: i_success_startDate,
                endDate: i_success_endDate,
                text: i_success_text,
                locale: i_success_locale,
                sourceIndicator: i_success_sourceIndicator,
                comment: i_success_comment
        )
        return informationText
    }


    private def newInvalidForCreateInformationText() {
        def informationText = new InformationText(
                pageName: i_failure_pageName,
                label: i_failure_label,
                textType: i_failure_textType,
                sequenceNumber: i_failure_sequenceNumber,
                persona: i_failure_persona,
                startDate: i_failure_startDate,
                endDate: i_failure_endDate,
                text: i_failure_text,
                locale: i_failure_locale,
                sourceIndicator: i_failure_sourceIndicator,
                comment: i_failure_comment
        )
        return informationText
    }

    private def setAuthentication() {
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('HOSWEB002', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
    }

    private def createBaselineWithSingleValueKey() {
        createInfoTextTestData(RECORD_BASELINE, "Baseline text no", "key1", new Date(), new Date(), 1)
    }

    private def createBaselineTestDataWithNotNullDate() {
        createInfoTextTestData(RECORD_BASELINE, "Baseline text no", "key1", new Date(), new Date())
        createInfoTextTestData(RECORD_BASELINE, "Baseline second text no", "key2", new Date(), new Date())
    }

    private def createLocalWithSingleValueKey() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1", new Date(), new Date(), 1)
    }

    def createLocalTestDataWithNotNullDate() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1", new Date(), new Date())
        createInfoTextTestData(RECORD_LOCAL, "Local second text no", "key2", new Date(), new Date())
    }

    def createLocalTestDataWithFutureStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        def startDate = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 5)
        def endDate = calendar.getTime()
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1", startDate, endDate)

    }

    def createLocalTestDataWithNullDate() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1")
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key2")
    }

    def createLocalTestDataWithSingleNullDate() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1", new Date(), new Date(), 4, true)
    }

    def createSingleLocalTestDataForWebUser() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1", new Date(), new Date(), 1, false, PERSONA_WEBUSER)
    }

    def createMultipleLocalTestDataForWebUser() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key1", new Date(), new Date(), 3, true, PERSONA_WEBUSER)
    }

    def createSingleDefaultLocalTestDataForUser() {
        createInfoTextTestData(RECORD_LOCAL, "DEFAULT - Local text no", "key1", new Date(), new Date(), 1, false, PERSONA_DEFAULT)
    }

    def createMultipleDefaultLocalTestDataForUser() {
        createInfoTextTestData(RECORD_LOCAL, "DEFAULT - Local text no", "key1", new Date(), new Date(), 3, true, PERSONA_DEFAULT)
    }

    def createInfoTextTestData(sourceIndicator, text, label, startDate = null, endDate = null, recordsSize = 4, singleNullDateIndicator = false, persona = PERSONA_STUDENT) {
        def pageName = PAGE_NAME
        def textType = "N"
        def sequenceNumber = 1
        def locale = "en_US"
        def comment = "Test data"
        recordsSize.times {
            if (singleNullDateIndicator) {
                if (it == recordsSize - 1) {
                    startDate = null
                    endDate = null
                }
            }
            new InformationText(
                    pageName: pageName,
                    label: label,
                    textType: textType,
                    sequenceNumber: sequenceNumber++,
                    persona: persona,
                    startDate: startDate,
                    endDate: endDate,
                    text: text + " " + it,
                    locale: locale,
                    sourceIndicator: sourceIndicator,
                    comment: comment
            ).save(failOnError: true, flush: true)

        }
    }

    private def isSsbEnabled() {
        Holders.config.ssbEnabled instanceof Boolean ? Holders.config.ssbEnabled : false
    }
}


