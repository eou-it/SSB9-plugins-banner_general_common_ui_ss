/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.overall.loginworkflow

import net.hedtech.banner.general.person.PersonBasicPersonBase
import net.hedtech.banner.general.system.SdaCrosswalkConversion
import net.hedtech.banner.utility.DateUtility
import org.apache.log4j.Logger
import java.sql.Timestamp
import java.sql.Date
import net.hedtech.banner.security.BannerGrantedAuthorityService

class SurveyFlow extends PostLoginWorkflow {
    def sessionFactory
    private final log = Logger.getLogger(getClass())
    private static final STUDENT_ROLE = "SELFSERVICE-STUDENT"
    private static final EMPLOYEE_ROLE = "SELFSERVICE-EMPLOYEE"
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
        def authorities = BannerGrantedAuthorityService.getAuthorities()
        def userAuthorities = authorities?.collect { it.objectName }
        return (userAuthorities?.contains(STUDENT_ROLE) || userAuthorities?.contains(EMPLOYEE_ROLE))
    }

    private def getSurveyConfirmedIndicator(pidm) {
        return PersonBasicPersonBase.fetchSurveyConfirmedFlagByPidm(pidm);
    }

}
