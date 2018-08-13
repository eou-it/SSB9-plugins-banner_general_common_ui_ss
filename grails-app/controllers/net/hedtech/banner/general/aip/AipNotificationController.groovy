/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.general.aip

import grails.converters.JSON
import net.hedtech.banner.general.ConfigurationData

import static net.hedtech.banner.general.aip.AipNotificationConstants.ENABLED;

/**
 * AIP Notification Controller Class it has end point for notification
 * */

class AipNotificationController {

    def aipNotificationService
    def springSecurityService
    static defaultAction = "checkNotification"

    /**
     *  Default end point for AIP Notificaton Controller
     * @return
     * */
    def checkNotification() {
        def user
        def model = [url: null, hasActiveActionItems: false, isNotificationDisplayed: false]

        if (!springSecurityService.isLoggedIn()) {
            response.sendError(403)
        } else {
            user = springSecurityService.getAuthentication()?.user
            if (!isNotificationShown(session)) {
                model.isNotificationDisplayed = false
                model.hasActiveActionItems = hasActiveActionItems(user.pidm)
                model.url = model.hasActiveActionItems ? getAipUrl() : null
                session.setAttribute("isNotificationShown", true)
            } else {
                model.isNotificationDisplayed = true
            }
            render model as JSON
        }
    }

    /**
     * Checks whether the user has active action items
     * @param pidm
     * @return Boolean
     * */
    private boolean hasActiveActionItems(Integer pidm) {
        return isAipEnabled() ? aipNotificationService.hasActiveActionItems(pidm) : false
    }

    /**
     * Checks whether aip is enabled or not
     * @return Boolean
     * */
    private Boolean isAipEnabled() {
        String sessionAipEnabledStatus = session.getAttribute("aipEnabledStatus")
        def gorIccrFlag

        if (!sessionAipEnabledStatus) {
            //For non aip aware applications we dont have aipEnabledStatus set in the session
            gorIccrFlag = aipNotificationService.getGoriicrFlag()
            //setting the session for the user
            session["aipEnabledStatus"] = gorIccrFlag
            sessionAipEnabledStatus = session["aipEnabledStatus"]
        }
        return sessionAipEnabledStatus.equals(ENABLED) ? true : false
    }

    /**
     * Checks whether isNotificationShown flag is set in the session or not and returs a boolean value
     * @param session
     * @return
     * */
    private Boolean isNotificationShown(def session) {
        return session.getAttribute("isNotificationShown") ? true : false
    }

    /**
     * Returns the general location from gurocfg table
     * @return String
     * */
    private def getAipUrl() {
        ConfigurationData getGeneralLocation = ConfigurationData.fetchByNameAndType('GENERALLOCATION', 'string', 'GENERAL_SS')
        return getGeneralLocation ? getGeneralLocation.value + 'ssb/aip/' : null
    }


}