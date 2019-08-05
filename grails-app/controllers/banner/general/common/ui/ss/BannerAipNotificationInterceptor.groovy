package banner.general.common.ui.ss
/*******************************************************************************
 Copyright 2014-2019 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

import net.hedtech.banner.general.configuration.ConfigProperties
import org.grails.web.servlet.GrailsUrlPathHelper

import java.sql.SQLException

import static net.hedtech.banner.general.aip.AipNotificationConstants.ENABLED

class BannerAipNotificationInterceptor {
    def aipNotificationService
    def springSecurityService
    private static final String SLASH = "/"
    private static final String QUESTION_MARK = "?"

    int order = HIGHEST_PRECEDENCE + 60

    BannerAipNotificationInterceptor() {
        matchAll()
                .excludes(controller: 'selfServiceMenu')
                .excludes(controller: 'login')
                .excludes(controller: 'logout')
                .excludes(controller: 'error')
                .excludes(controller: 'dateConverter')
                .excludes(controller: 'about')
                .excludes(controller: 'theme')
                .excludes(controller: 'userPreference')
                .excludes(controller: 'shortcut')
                .excludes(controller: 'restfulApi')
                .excludes(controller: 'cssRender')

    }

    boolean before() {
        String path = getServletPath(request)
        if (springSecurityService.isLoggedIn() && path != null){
            if(session.getAttribute("hasActiveActionItems") == null){
                def user = springSecurityService.getAuthentication()?.user
                boolean hasActiveActionItems = hasActiveActionItems(user.pidm,session)
                session.setAttribute("hasActiveActionItems",hasActiveActionItems)
                if(hasActiveActionItems){
                    session.setAttribute("aipUrl", getAipUrl())
                }
            }else{
                session.setAttribute("hasActiveActionItems",false)
            }

        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }

    /**
     * Checks whether the user has active action items
     * @param pidm
     * @return Boolean
     * */
    private boolean hasActiveActionItems(Integer pidm,session) {
        return isAipEnabled(session) ? aipNotificationService.hasActiveActionItems(pidm) : false
    }

    /**
     * Checks whether aip is enabled or not
     * @return Boolean
     * */
    private Boolean isAipEnabled(session) {
        String sessionAipEnabledStatus = session.getAttribute("aipEnabledStatus")
        def gorIccrFlag
        if (!sessionAipEnabledStatus) {
            //For non aip aware applications we dont have aipEnabledStatus set in the session
            gorIccrFlag = aipNotificationService.getAipEnabledFlag()
            //setting the session for the user
            session["aipEnabledStatus"] = gorIccrFlag
            sessionAipEnabledStatus = session["aipEnabledStatus"]
        }
        return sessionAipEnabledStatus.equals(ENABLED) ? true : false
    }


    /**
     * Returns the general location from gurocfg table
     * @return String
     * */
    private def getAipUrl() {
        try{
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('GENERALLOCATION','GENERAL_SS')
            return configProperties? configProperties.configValue + '/ssb/aip/' : null
        }catch (SQLException e){
            log.warn("Unable to fetch the configuration GENERALLOCATION "+e.getMessage())
            return ""
        }
    }


    /**
     * Returns the request url path
     * @return String
     * */
    private getServletPath(request) {
        GrailsUrlPathHelper urlPathHelper = new GrailsUrlPathHelper()
        String path = urlPathHelper.getOriginatingRequestUri(request)
        if (path != null) {
            path = path.substring(request.getContextPath().length())
            if (SLASH.equals(path)) {
                path = null
            }
            else if (request?.getQueryString()) {
                path = path + QUESTION_MARK + request?.getQueryString()
            }
        }
        return path
    }
}
