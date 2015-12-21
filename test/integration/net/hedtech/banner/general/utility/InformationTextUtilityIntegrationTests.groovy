/*********************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
**********************************************************************************/

package net.hedtech.banner.general.utility
import org.junit.Before
import org.junit.Test
import org.junit.After

import grails.util.Holders
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Locale

class InformationTextUtilityIntegrationTests extends BaseIntegrationTestCase {

    def selfServiceBannerAuthenticationProvider
    private static final String PAGE_NAME = "TESTPAGE"
    private static final String PERSONA_STUDENT = "STUDENT"
    private static final String PERSONA_WEBUSER = "WEBUSER"
    private static final String PERSONA_DEFAULT = "DEFAULT"
    private static final String RECORD_BASELINE = "B"
    private static final String RECORD_LOCAL = "L"

	@Before
	public void setUp() {
        if (!isSsbEnabled()) return
        formContext = ['GUAGMNU'] // Since we are not testing a controller, we need to explicitly set this
        super.setUp()
    }


	@After
	public void tearDown() {
        if (!isSsbEnabled()) return
        super.tearDown()
    }

	@Test
    void testSingleValueKeyWithBaseline() {
        createBaselineWithSingleValueKey()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Baseline text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

	@Test
    void testMultipleValuesKeyWithBaseline() {
        createBaselineTestDataWithNotNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String value1 = "Baseline text no 0\nBaseline text no 1\nBaseline text no 2\nBaseline text no 3"
        String value2 = "Baseline second text no 0\nBaseline second text no 1\nBaseline second text no 2\nBaseline second text no 3"
        assertEquals(value1, informationText.key1)
        assertEquals(value2, informationText.key2)
        logout()
    }

	@Test
    void testSingleKeyWithoutValue() {
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = ""
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

	@Test
    void testSingleValueKeyWithLocal() {
        createLocalWithSingleValueKey()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

	@Test
    void testMultipleValuesKeyWithLocalNullDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String expectedText1 = "Baseline text no 0\nBaseline text no 1\nBaseline text no 2\nBaseline text no 3"
        String expectedText2 = "Baseline second text no 0\nBaseline second text no 1\nBaseline second text no 2\nBaseline second text no 3"
        GroovyTestCase.assertEquals(expectedText1, informationText.key1)
        GroovyTestCase.assertEquals(expectedText2, informationText.key2)
        logout()
    }

	@Test
    void testMultipleValuesKeyWithLocalNotNullDate() {
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

	@Test
    void testMultipleValuesKeyWithLocalSingleNullDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithSingleNullDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Local text no 0\nLocal text no 1\nLocal text no 2"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

	@Test
    void testLocalWithFutureStartDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithFutureStartDate()
        setAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Baseline text no 0\nBaseline text no 1\nBaseline text no 2\nBaseline text no 3"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }


	@Test
    void testAnonymousUserSingleValue() {
        setAuthentication()
        createSingleLocalTestDataForWebUser();
        logout()
        setAnonymousAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1")
        String expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

	@Test
    void testAnonymousUserMultipleValues() {
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

	@Test
    void testDefaultPersonaSingleValue() {
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

	@Test
    void testDefaultPersonaMultipleValue() {
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

    @Test
    void testFallbackLocales() {
        List<Locale> fallbackLocales = InformationTextUtility.getFallbackLocaleNames(Locale.CANADA_FRENCH)
        assertEquals(3, fallbackLocales.size())
        assertEquals("fr_CA", fallbackLocales[0])
        assertEquals("fr", fallbackLocales[1])
        assertEquals(Locale.default.toString(), fallbackLocales[2])

    }

    @Test
    void testLocalOnlyLocaleMatch(){
        createSingleDefaultLocalTestDataForUserForLocale()
        setAuthentication()
        Locale l = new Locale("fr","CA")
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1",l)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    @Test
    void testBaseLineOnlyLocaleMatch(){
        createSingleDefaultLocalTestDataForUserWithLocale()
        createBaselineWithSingleValueKeyWithLocale()
        setAuthentication()
        Locale l = new Locale("fr","CA")
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1",l)
        String expectedText = "Baseline text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    @Test
    void testLocalAndBaseLineLocaleMatch(){
        createSingleDefaultLocalTestDataForUserForLocale()
        createBaselineWithSingleValueKeyForLocale()
        setAuthentication()
        Locale l = new Locale("fr","CA")
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1",l)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    @Test
    void testFallbackLocaleMatch(){
        createSingleDefaultLocalTestDataForUserWithLocale()
        createBaselineWithSingleValueKeyWithFallbackLocale()
        setAuthentication()
        Locale l = new Locale("fr","CA")
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, "key1",l)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    @Test
    void testFallbackForLabelWhenMultiplePageKeysHavingMultipleBaselineLocaleRecords(){

        final def TESTPAGE = "TESTPAGE"
        def Key1 = "Key1"
        createInfoTextRecord(TESTPAGE, Key1,"N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr", "fr", "B", "Test data")
        createInfoTextRecord(TESTPAGE,Key1,"N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr_CA", "fr_CA", "B", "Test data")
        createInfoTextRecord(TESTPAGE,"Key2","N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key2 for fr", "fr", "B", "Test data")
        createInfoTextRecord(TESTPAGE,"Key3","N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr", "fr", "B", "Test data")
        createInfoTextRecord(TESTPAGE,"Key3","N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr_CA", "fr_CA", "B", "Test data")

        setAuthentication()

        def informationText = InformationTextUtility.getMessage(TESTPAGE, Key1,new Locale("fr"))
        GroovyTestCase.assertEquals("Failed for direct match for language","Baseline text for Key1 for fr", informationText)

        informationText = InformationTextUtility.getMessage(TESTPAGE, Key1,new Locale("fr","CA"))
        GroovyTestCase.assertEquals("Failed for direct match for language_country","Baseline text for Key1 for fr_CA", informationText)

        informationText = InformationTextUtility.getMessage(TESTPAGE, Key1,new Locale("fr","MX"))
        GroovyTestCase.assertEquals("Failed for fallback to language","Baseline text for Key1 for fr", informationText)

        logout()
    }

    @Test
    void testFallbackForLabelsWithinPageWhenMultiplePageKeysHavingMultipleBaselineLocaleRecords(){

        final def TESTPAGE = "TESTPAGE"
        def Key1 = "Key1"
        createInfoTextRecord(TESTPAGE, Key1,"N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr", "fr", "B", "Test data")
        createInfoTextRecord(TESTPAGE,Key1,"N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr_CA", "fr_CA", "B", "Test data")
        createInfoTextRecord(TESTPAGE,"Key2","N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key2 for fr", "fr", "B", "Test data")
        createInfoTextRecord(TESTPAGE,"Key3","N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr", "fr", "B", "Test data")
        createInfoTextRecord(TESTPAGE,"Key3","N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr_CA", "fr_CA", "B", "Test data")

        setAuthentication()

        def infoTextMap = InformationTextUtility.getMessages(TESTPAGE, new Locale("fr", "CA"))
        GroovyTestCase.assertNotNull("info-text for Key1 is not retrieved",infoTextMap.Key1)
        GroovyTestCase.assertNotNull("info-text for Key2 is not retrieved",infoTextMap.Key2)
        GroovyTestCase.assertNotNull("info-text for Key3 is not retrieved",infoTextMap.Key3)

        GroovyTestCase.assertEquals("Failed for direct match for language","Baseline text for Key1 for fr_CA", infoTextMap.Key1)
        GroovyTestCase.assertEquals("Failed for direct match for language","Baseline text for Key2 for fr", infoTextMap."Key2")
        GroovyTestCase.assertEquals("Failed for direct match for language","Baseline text for Key3 for fr_CA", infoTextMap."Key3")

        logout()
    }

    private void createInfoTextRecord(pageName, label, textType, sequenceNumber, persona, startDate, endDate, text, locale, sourceIndicator, comment) {
        new InformationText(pageName: pageName, label: label, textType: textType, sequenceNumber: sequenceNumber, persona: persona,
                startDate: startDate, endDate: endDate, text: text, locale: locale, sourceIndicator: sourceIndicator, comment: comment
        ).save(failOnError: true, flush: true)
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
        def oldFlag = Holders.getConfig().ssbEnabled
        Holders.getConfig().ssbEnabled = true
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('HOSWEB002', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        Holders.getConfig().ssbEnabled = oldFlag
    }

    private def createBaselineWithSingleValueKey() {
        createInfoTextTestData(RECORD_BASELINE, "Baseline text no", "key1", new Date(), new Date(), 1)
    }

    private def createBaselineWithSingleValueKeyForLocale() {
        createInfoTextTestDataForLocaleMatch(RECORD_BASELINE, "Baseline text no", "key1", new Date(), new Date(), 1)
    }

    private def createBaselineWithSingleValueKeyWithLocale() {
        createInfoTextTestDataWithLocaleArgument(RECORD_BASELINE, "Baseline text no", "key1","fr_CA", new Date(), new Date(), 1)
    }

    private def createBaselineWithSingleValueKeyWithFallbackLocale() {
        createInfoTextTestDataWithLocaleArgument(RECORD_BASELINE, "Baseline text no", "key1","fr", new Date(), new Date(), 1)
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

    def createSingleDefaultLocalTestDataForUserForLocale() {
        createInfoTextTestDataForLocaleMatch(RECORD_LOCAL, "DEFAULT - Local text no", "key1", new Date(), new Date(), 1, false, PERSONA_DEFAULT)
    }

    def createSingleDefaultLocalTestDataForUserWithLocale() {
        createInfoTextTestDataWithLocaleArgument(RECORD_LOCAL, "DEFAULT - Local text no", "key1","fr", new Date(), new Date(), 1, false, PERSONA_DEFAULT)
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

    def createInfoTextTestDataForLocaleMatch(sourceIndicator, text, label, startDate = null, endDate = null, recordsSize = 4, singleNullDateIndicator = false, persona = PERSONA_STUDENT) {
        def pageName = PAGE_NAME
        def textType = "N"
        def sequenceNumber = 1
        def locale = "fr_CA"
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

    def createInfoTextTestDataWithLocaleArgument(sourceIndicator, text, label, locale, startDate = null, endDate = null, recordsSize = 4, singleNullDateIndicator = false, persona = PERSONA_STUDENT) {
        def pageName = PAGE_NAME
        def textType = "N"
        def sequenceNumber = 1
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


