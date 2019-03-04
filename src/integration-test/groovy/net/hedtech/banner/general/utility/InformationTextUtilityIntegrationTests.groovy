/*********************************************************************************
 Copyright 2014-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.general.utility

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

@Integration
@Rollback
class InformationTextUtilityIntegrationTests extends BaseIntegrationTestCase {

    private static final def PAGE_NAME = "TESTPAGE"
    private static final def PERSONA_STUDENT = "STUDENT"
    private static final def PERSONA_WEBUSER = "WEBUSER"
    private static final def PERSONA_DEFAULT = "DEFAULT"
    private static final def RECORD_BASELINE = "B"
    private static final def RECORD_LOCAL = "L"
    private static final def KEY_1 = "key1"
    private static final def LOCALE_FR = "fr"
    private static final def LOCALE_COUNTRY = "CA"
    private static final def ASSERT_VALUE_1 = 'Baseline text no 0\nBaseline text no 1\nBaseline text no 2\nBaseline text no 3'

    @Before
    public void setUp() {
        formContext = ['GUAGMNU'] // Since we are not testing a controller, we need to explicitly set this
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }

    @Test
    void testSingleValueKeyWithBaseline() {
        createBaselineWithSingleValueKey()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = "Baseline text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testMultipleValuesKeyWithBaseline() {
        createBaselineTestDataWithNotNullDate()

        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String value1 = ASSERT_VALUE_1
        String value2 = "Baseline second text no 0\nBaseline second text no 1\nBaseline second text no 2\nBaseline second text no 3"
        assertEquals(value1, informationText.key1)
        assertEquals(value2, informationText.key2)
    }

    @Test
    void testSingleKeyWithoutValue() {

        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = ""
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testSingleValueKeyWithLocal() {
        createLocalWithSingleValueKey()

        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testMultipleValuesKeyWithLocalNullDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithNullDate()

        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String expectedText1 = ASSERT_VALUE_1
        String expectedText2 = "Baseline second text no 0\nBaseline second text no 1\nBaseline second text no 2\nBaseline second text no 3"
        GroovyTestCase.assertEquals(expectedText1, informationText.key1)
        GroovyTestCase.assertEquals(expectedText2, informationText.key2)
    }

    @Test
    void testMultipleValuesKeyWithLocalNotNullDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithNotNullDate()

        def informationText = InformationTextUtility.getMessages(PAGE_NAME)
        String value1 = "Local text no 0\nLocal text no 1\nLocal text no 2\nLocal text no 3"
        String value2 = "Local second text no 0\nLocal second text no 1\nLocal second text no 2\nLocal second text no 3"
        assertEquals(value1, informationText.key1)
        assertEquals(value2, informationText.key2)
    }

    @Test
    void testMultipleValuesKeyWithLocalSingleNullDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithSingleNullDate()

        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = "Local text no 0\nLocal text no 1\nLocal text no 2"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testLocalWithFutureStartDate() {
        createBaselineTestDataWithNotNullDate()
        createLocalTestDataWithFutureStartDate()

        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = ASSERT_VALUE_1
        GroovyTestCase.assertEquals(expectedText, informationText)
    }


    @Test
    void testAnonymousUserSingleValue() {

        createSingleLocalTestDataForWebUser();
        setAnonymousAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    @Test
    void testAnonymousUserMultipleValues() {

        createMultipleLocalTestDataForWebUser();
        setAnonymousAuthentication()
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        println informationText
        String expectedText = "Local text no 0\nLocal text no 1"
        GroovyTestCase.assertEquals(expectedText, informationText)
        logout()
    }

    @Test
    void testDefaultPersonaSingleValue() {

        createSingleDefaultLocalTestDataForUser();
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)

        createLocalWithSingleValueKey()
        informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        expectedText = "Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testDefaultPersonaMultipleValue() {

        createMultipleDefaultLocalTestDataForUser();
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        String expectedText = "DEFAULT - Local text no 0\n" +
                "DEFAULT - Local text no 1"
        GroovyTestCase.assertEquals(expectedText, informationText)

        createLocalTestDataWithNotNullDate()
        informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1)
        expectedText = "Local text no 0\nLocal text no 1\nLocal text no 2\nLocal text no 3"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testFallbackLocales() {
        List<Locale> fallbackLocales = InformationTextUtility.getFallbackLocaleNames(Locale.CANADA_FRENCH)
        assertEquals(3, fallbackLocales.size())
        assertEquals("fr_CA", fallbackLocales[0])
        assertEquals(LOCALE_FR, fallbackLocales[1])
        assertEquals(Locale.default.toString(), fallbackLocales[2])
    }

    @Test
    void testLocalOnlyLocaleMatch() {
        createSingleDefaultLocalTestDataForUserForLocale()
        Locale l = new Locale(LOCALE_FR, LOCALE_COUNTRY)
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1, l)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testBaseLineOnlyLocaleMatch() {
        createSingleDefaultLocalTestDataForUserWithLocale()
        createBaselineWithSingleValueKeyWithLocale()

        Locale l = new Locale(LOCALE_FR, LOCALE_COUNTRY)
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1, l)
        String expectedText = "Baseline text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testLocalAndBaseLineLocaleMatch() {
        createSingleDefaultLocalTestDataForUserForLocale()
        createBaselineWithSingleValueKeyForLocale()
        Locale l = new Locale(LOCALE_FR, LOCALE_COUNTRY)
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1, l)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testFallbackLocaleMatch() {
        createSingleDefaultLocalTestDataForUserWithLocale()
        createBaselineWithSingleValueKeyWithFallbackLocale()
        Locale l = new Locale(LOCALE_FR, LOCALE_COUNTRY)
        def informationText = InformationTextUtility.getMessage(PAGE_NAME, KEY_1, l)
        String expectedText = "DEFAULT - Local text no 0"
        GroovyTestCase.assertEquals(expectedText, informationText)
    }

    @Test
    void testFallbackWithLabelForRecordsWithMultipleLabels() {

        final def TESTPAGE = PAGE_NAME
        def Key1 = "Key1"
        createInfoTextRecord(TESTPAGE, Key1, "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr", LOCALE_FR, "B", "Test data")
        createInfoTextRecord(TESTPAGE, Key1, "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr_CA", "fr_CA", "B", "Test data")
        createInfoTextRecord(TESTPAGE, "Key2", "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key2 for fr", LOCALE_FR, "B", "Test data")
        createInfoTextRecord(TESTPAGE, "Key3", "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr", LOCALE_FR, "B", "Test data")
        createInfoTextRecord(TESTPAGE, "Key3", "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr_CA", "fr_CA", "B", "Test data")

        def informationText = InformationTextUtility.getMessage(TESTPAGE, Key1, new Locale(LOCALE_FR))
        GroovyTestCase.assertEquals("Failed for direct match for language", "Baseline text for Key1 for fr", informationText)

        informationText = InformationTextUtility.getMessage(TESTPAGE, Key1, new Locale(LOCALE_FR, LOCALE_COUNTRY))
        GroovyTestCase.assertEquals("Failed for direct match for language_country", "Baseline text for Key1 for fr_CA", informationText)

        informationText = InformationTextUtility.getMessage(TESTPAGE, Key1, new Locale(LOCALE_FR, "MX"))
        GroovyTestCase.assertEquals("Failed for fallback to language", "Baseline text for Key1 for fr", informationText)
    }

    @Test
    void testFallbackForRecordsWithMultipleLabels() {
        final def TESTPAGE = PAGE_NAME
        def Key1 = "Key1"
        createInfoTextRecord(TESTPAGE, Key1, "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr", LOCALE_FR, "B", "Test data")
        createInfoTextRecord(TESTPAGE, Key1, "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key1 for fr_CA", "fr_CA", "B", "Test data")
        createInfoTextRecord(TESTPAGE, "Key2", "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key2 for fr", LOCALE_FR, "B", "Test data")
        createInfoTextRecord(TESTPAGE, "Key3", "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr", LOCALE_FR, "B", "Test data")
        createInfoTextRecord(TESTPAGE, "Key3", "N", 1, PERSONA_STUDENT, null, null, "Baseline text for Key3 for fr_CA", "fr_CA", "B", "Test data")

        def infoTextMap = InformationTextUtility.getMessages(TESTPAGE, new Locale(LOCALE_FR, LOCALE_COUNTRY))
        GroovyTestCase.assertNotNull("info-text for Key1 is not retrieved", infoTextMap.Key1)
        GroovyTestCase.assertNotNull("info-text for Key2 is not retrieved", infoTextMap.Key2)
        GroovyTestCase.assertNotNull("info-text for Key3 is not retrieved", infoTextMap.Key3)

        GroovyTestCase.assertEquals("Failed for direct match for language", "Baseline text for Key1 for fr_CA", infoTextMap.Key1)
        GroovyTestCase.assertEquals("Failed for direct match for language", "Baseline text for Key2 for fr", infoTextMap."Key2")
        GroovyTestCase.assertEquals("Failed for direct match for language", "Baseline text for Key3 for fr_CA", infoTextMap."Key3")
    }

    /************************************************Helpers***********************************************************/
    private void createInfoTextRecord(pageName, label, textType, sequenceNumber, persona, startDate, endDate, text, locale, sourceIndicator, comment) {
        new InformationText(pageName: pageName, label: label, textType: textType, sequenceNumber: sequenceNumber, persona: persona,
                startDate: startDate, endDate: endDate, text: text, locale: locale, sourceIndicator: sourceIndicator, comment: comment
        ).save(failOnError: true, flush: true)
    }

    void setAnonymousAuthentication() {
        List roles = new ArrayList();
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
        roles.add(grantedAuthority);
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("anonymousUser", "anonymousUser", roles);
        SecurityContextHolder.getContext().setAuthentication(auth)
    }

    private def createBaselineWithSingleValueKey() {
        createInfoTextTestData(RECORD_BASELINE, "Baseline text no", KEY_1, new Date(), new Date(), 1)
    }

    private def createBaselineWithSingleValueKeyForLocale() {
        createInfoTextTestDataForLocaleMatch(RECORD_BASELINE, "Baseline text no", KEY_1, new Date(), new Date(), 1)
    }

    private def createBaselineWithSingleValueKeyWithLocale() {
        createInfoTextTestDataWithLocaleArgument(RECORD_BASELINE, "Baseline text no", KEY_1, "fr_CA", new Date(), new Date(), 1)
    }

    private def createBaselineWithSingleValueKeyWithFallbackLocale() {
        createInfoTextTestDataWithLocaleArgument(RECORD_BASELINE, "Baseline text no", KEY_1, LOCALE_FR, new Date(), new Date(), 1)
    }

    private def createBaselineTestDataWithNotNullDate() {
        createInfoTextTestData(RECORD_BASELINE, "Baseline text no", KEY_1, new Date(), new Date())
        createInfoTextTestData(RECORD_BASELINE, "Baseline second text no", "key2", new Date(), new Date())
    }

    private def createLocalWithSingleValueKey() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1, new Date(), new Date(), 1)
    }

    private def createLocalTestDataWithNotNullDate() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1, new Date(), new Date())
        createInfoTextTestData(RECORD_LOCAL, "Local second text no", "key2", new Date(), new Date())
    }

    private def createLocalTestDataWithFutureStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        def startDate = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 5)
        def endDate = calendar.getTime()
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1, startDate, endDate)

    }

    private def createLocalTestDataWithNullDate() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1)
        createInfoTextTestData(RECORD_LOCAL, "Local text no", "key2")
    }

    private def createLocalTestDataWithSingleNullDate() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1, new Date(), new Date(), 4, true)
    }

    private def createSingleLocalTestDataForWebUser() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1, new Date(), new Date(), 1, false, PERSONA_WEBUSER)
    }

    private def createMultipleLocalTestDataForWebUser() {
        createInfoTextTestData(RECORD_LOCAL, "Local text no", KEY_1, new Date(), new Date(), 3, true, PERSONA_WEBUSER)
    }

    private def createSingleDefaultLocalTestDataForUser() {
        createInfoTextTestData(RECORD_LOCAL, "DEFAULT - Local text no", KEY_1, new Date(), new Date(), 1, false, PERSONA_DEFAULT)
    }

    private def createSingleDefaultLocalTestDataForUserForLocale() {
        createInfoTextTestDataForLocaleMatch(RECORD_LOCAL, "DEFAULT - Local text no", KEY_1, new Date(), new Date(), 1, false, PERSONA_DEFAULT)
    }

    private def createSingleDefaultLocalTestDataForUserWithLocale() {
        createInfoTextTestDataWithLocaleArgument(RECORD_LOCAL, "DEFAULT - Local text no", KEY_1, LOCALE_FR, new Date(), new Date(), 1, false, PERSONA_DEFAULT)
    }

    private def createMultipleDefaultLocalTestDataForUser() {
        createInfoTextTestData(RECORD_LOCAL, "DEFAULT - Local text no", KEY_1, new Date(), new Date(), 3, true, PERSONA_DEFAULT)
    }

    private
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

    private
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

    private
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

}


