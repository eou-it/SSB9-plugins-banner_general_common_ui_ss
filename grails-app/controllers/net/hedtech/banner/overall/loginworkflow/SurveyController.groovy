/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.overall.loginworkflow

import net.hedtech.banner.general.person.PersonBasicPersonBase
import net.hedtech.banner.general.person.PersonRace
import net.hedtech.banner.general.system.Race
import net.hedtech.banner.general.system.RegulatoryRace
import net.hedtech.banner.general.utility.InformationTextUtility
import net.hedtech.banner.security.BannerGrantedAuthorityService

class SurveyController {
    static defaultAction = "index"
    def surveyService
    String PAGE_NAME = "RESURVEY"
    String RACE_KEY = "race.header"
    String ETHNICITY_KEY = "ethnicity.header"
    public static final SURVEY_ACTION = "surveydone"
    public static final ACTION_DONE = "true"
    private static final String SLASH = "/"
    private static final String VIEW = "survey"
    def ssbLoginURLRequest

    def survey() {
        def pidm = BannerGrantedAuthorityService.getPidm()
        def raceMap = [:]
        def regulatoryRaces = RegulatoryRace.fetchRequiredRegulatoryRaces()
        regulatoryRaces.each { regulatoryRace ->
            def races = []
            races = Race.fetchAllByRegulatoryRace(regulatoryRace.code)
            if (!races.isEmpty()) {
                raceMap.put(regulatoryRace.code, races)
            }
        }
        // Get all Race Categories by system indicator = 'Y'
        // Loop over the Categories, get the Races under each Category and put them inside model.
        // Find a way to differentiate selected Races by the User from the not selected Races.
        def personRaces = PersonRace.fetchByPidm(pidm)
        def personRaceCodes = []
        personRaces?.each {
            personRaceCodes.add(it.race)
        }
        def personBasicPersonBase = PersonBasicPersonBase.fetchByPidm(pidm)
        def personEthnicity = personBasicPersonBase?.ethnic
        session.setAttribute("raceMap", raceMap)
        session.setAttribute("regulatoryRaces", regulatoryRaces)
        def infoTexts = ["ethnicity.header": InformationTextUtility.getMessage(PAGE_NAME, ETHNICITY_KEY), "race.header": InformationTextUtility.getMessage(PAGE_NAME, RACE_KEY)]
        def model = [raceMap: raceMap, regulatoryRaces: regulatoryRaces, personRaceCodes: personRaceCodes, personEthnicity: personEthnicity, postUrl: "${request.contextPath}/survey/save", infoTexts: infoTexts]
        render view: VIEW, model: model
    }


    def save = {
        def pidm = BannerGrantedAuthorityService.getPidm()
        surveyService.saveSurveyResponse(pidm, params.ethnicity, params.race)
        done()
    }

    def done() {
        request.getSession().setAttribute(SURVEY_ACTION, ACTION_DONE)
        String path = request.getSession().getAttribute(PostLoginWorkflow.URI_ACCESSED)
        if (path == null) {
            path = SLASH
        } else {
            path = checkPath(path)
        }
        request.getSession().setAttribute(PostLoginWorkflow.URI_REDIRECTED, path)
        redirect uri: path
    }

    protected String checkPath(String path) {
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(path)
        List<PostLoginWorkflow> listOfFlows = PostLoginWorkflow.getListOfFlows()
        for (PostLoginWorkflow flow : listOfFlows) {
            if (flow.getControllerName().equals(controllerName)) {
                path = SLASH
                return path
            }
        }
        return path
    }
}
