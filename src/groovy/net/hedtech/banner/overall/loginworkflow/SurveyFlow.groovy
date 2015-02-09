/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.overall.loginworkflow

import net.hedtech.banner.general.person.PersonBasicPersonBase
import net.hedtech.banner.general.system.SdaCrosswalkConversion
import net.hedtech.banner.utility.DateUtility
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import java.sql.Timestamp
import java.sql.Date
import net.hedtech.banner.security.BannerGrantedAuthorityService

class SurveyFlow extends PostLoginWorkflow {
    def sessionFactory
    private static final PAGE = "/ssb/survey/\\**"
    private static final CONFIRMATION_INDICATOR = "Y"
    protected static final int INTERNAL_SEQUENCE_NUMBER = 1
    protected static final String INTERNAL_GROUP = 'SSMREDATE'
    protected static final String SYSTEM_REQUEST_INDICATOR = 'Y'

    @Override
    public boolean isShowPage(request) {
        def pidm = BannerGrantedAuthorityService.getPidm()
        def session = request.getSession()
        String isDone = session.getAttribute(SurveyController.SURVEY_ACTION)
        boolean pushSurvey = false

        if (isSurveyAvailableForUserAuthority() && isDone != SurveyController.ACTION_DONE) {
            // Survey is not yet taken.
            Map startAndEndDates = SdaCrosswalkConversion.fetchReportingDates(INTERNAL_SEQUENCE_NUMBER,INTERNAL_GROUP,SYSTEM_REQUEST_INDICATOR);
            Timestamp surveyStartDate
            Timestamp surveyEndDate
            Timestamp today = new Timestamp(DateUtility.getTodayDate().getTime())
            if(startAndEndDates){
                Date startDate = startAndEndDates.RESTARTDAT
                Date endDate = startAndEndDates.REENDDATE
                if (startDate){
                    surveyStartDate =  new Timestamp(startDate.getTime())
                }
                if(endDate){
                    surveyEndDate = new Timestamp(endDate.getTime())
                }
            }
            // Survey start date is not null & Today is between Survey start and end dates
            if (surveyStartDate && (surveyStartDate <= today) && (surveyEndDate==null || (today <= surveyEndDate))) {
                if (getSurveyConfirmedIndicator(pidm) != CONFIRMATION_INDICATOR) {
                    pushSurvey = true
                }
            }
            return pushSurvey
        } else {
            // Do not show Survey page as Survey has already been taken.
            return pushSurvey
        }
    }

    public String getControllerUri() {
        return "/ssb/survey/survey"
    }

    public String getControllerName() {
        return "survey"
    }

    private static def isSurveyAvailableForUserAuthority() {
        def isAuthorized =false
        def pageRoles
        def authorities = BannerGrantedAuthorityService.getAuthorities()
        def userAuthorities = authorities?.collect { it.toString()}
        def pageDetail = ConfigurationHolder.config.grails.plugins.springsecurity.interceptUrlMap
        pageRoles = pageDetail.find {
            it =~ PAGE
        }?.value

        isAuthorized =  userAuthorities?.grep(pageRoles)

        return isAuthorized
    }

    private def getSurveyConfirmedIndicator(pidm) {
        return PersonBasicPersonBase.fetchSurveyConfirmedFlagByPidm(pidm);
    }

}
