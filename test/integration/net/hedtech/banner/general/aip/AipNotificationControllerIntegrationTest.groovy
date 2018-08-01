package net.hedtech.banner.general.aip

import grails.converters.JSON
import groovy.sql.Sql
import net.hedtech.banner.general.overall.IntegrationConfiguration
import org.codehaus.groovy.grails.web.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.banner.general.aip.AipNotificationController

import static net.hedtech.banner.general.aip.AipNotificationUtil.ICSN_CODE_ENABLE_ACTION_ITEMS
import static net.hedtech.banner.general.aip.AipNotificationUtil.NO
import static net.hedtech.banner.general.aip.AipNotificationUtil.SQPR_CODE_GENERAL_SSB
import static net.hedtech.banner.general.aip.AipNotificationUtil.YES
import static net.hedtech.banner.general.aip.AipNotificationUtil.ENABLED
import static net.hedtech.banner.general.aip.AipNotificationUtil.DISABLED
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue


class AipNotificationControllerIntegrationTest extends BaseIntegrationTestCase {

    @Before
    public void setUp() {
        formContext = ['GUAGMNU'] // Since we are not testing a controller, we need to explicitly set this
        controller = new AipNotificationController()
        super.setUp()

    }


    @After
    public void tearDown() {
        logout()
        super.tearDown()
    }


    @Test
    public void checkNotificationWhenNotLoggedIn() {
        logout()
        controller.checkNotification()
        assertEquals 403, controller.response.status.value
    }


    @Test
    public void testCheckNotificationWhenAIPEnabled() {
        def oldValue = getGoriicrValue(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS);
        assertNotNull oldValue

        updateGoriccrRule(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS, YES)

        // AIP enabled with action items and first time log in
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU001', '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        controller.checkNotification()

        assertNotNull controller.response;

        def response = controller.response.contentAsString
        assertEquals 200, controller.response.status.value
        def data = JSON.parse(response)

        assertEquals 3, data.size()
        assertTrue data.hasActiveActionItems
        assertFalse data.isNotificationDisplayed
        assertNotNull data.url

        assertEquals ENABLED, controller.session.request.session.attributes['aipEnabledStatus'].toString()
        assertTrue controller.session.getAttribute('isNotificationShown').toBoolean()
    }


    @Test
    public void testCheckNotificationWhenAIPDisabled() {
        def oldValue = getGoriicrValue(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS);
        assertNotNull oldValue

        updateGoriccrRule(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS, NO)

        // AIP enabled with action items and first time log in
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU001', '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        controller.checkNotification()

        assertNotNull controller.response;

        def response = controller.response.contentAsString
        assertEquals 200, controller.response.status.value
        def data = JSON.parse(response)

        assertEquals 3, data.size()
        assertFalse data.hasActiveActionItems
        assertFalse data.isNotificationDisplayed
        assertEquals JSONObject.NULL, data.url

        assertEquals DISABLED, controller.session.getAttribute('aipEnabledStatus').toString()
        assertTrue controller.session.getAttribute('isNotificationShown').toBoolean()
    }


    @Test
    public void testCheckNotificationAfterDismissing() {
        def oldValue = getGoriicrValue(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS);
        assertNotNull oldValue

        updateGoriccrRule(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS, YES)

        // AIP enabled with action items and first time log in
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU001', '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)

        controller.session.setAttribute("isNotificationShown", true)
        controller.session.setAttribute("aipEnabledStatus", ENABLED)

        controller.checkNotification()

        def secondResponse = controller.response.contentAsString
        assertEquals 200, controller.response.status.value
        def data2 = JSON.parse(secondResponse)

        assertEquals 3, data2.size()
        assertFalse data2.hasActiveActionItems
        assertTrue data2.isNotificationDisplayed
        assertEquals JSONObject.NULL, data2.url
    }


    @Test
    public void testCheckNotificationWhenAIPEnabledNoActionItems() {

        def oldValue = getGoriicrValue(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS);
        assertNotNull oldValue

        updateGoriccrRule(SQPR_CODE_GENERAL_SSB, ICSN_CODE_ENABLE_ACTION_ITEMS, YES)

        // AIP enabled with action items and first time log in
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('AIPADM001', '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        controller.checkNotification()

        assertNotNull controller.response;

        def response = controller.response.contentAsString
        assertEquals 200, controller.response.status.value
        def data = JSON.parse(response)

        assertEquals 3, data.size()
        assertFalse data.hasActiveActionItems
        assertFalse data.isNotificationDisplayed
        assertNotNull data.url

        assertEquals ENABLED, controller.session.request.session.attributes['aipEnabledStatus'].toString()
        assertTrue controller.session.getAttribute('isNotificationShown').toBoolean()

    }


    private def getGoriicrValue(def sqpr_code, def icsn_code) {
        IntegrationConfiguration integrationConfiguration = IntegrationConfiguration.fetchByProcessCodeAndSettingName(sqpr_code, icsn_code)
        integrationConfiguration?.value
    }

    /*
    * This method will set the GORRICCR Rule
    * */

    private void updateGoriccrRule(def sqpr_code, def icsn_code, def value) {
        IntegrationConfiguration integrationConfiguration = IntegrationConfiguration.fetchByProcessCodeAndSettingName(sqpr_code, icsn_code)
        integrationConfiguration.value = value
        integrationConfiguration.save(flush: true, failOnError: true)
    }
}