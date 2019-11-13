/*********************************************************************************
 Copyright 2014-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.overall.loginworkflow

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.person.PersonBasicPersonBase
import net.hedtech.banner.general.person.PersonRace
import net.hedtech.banner.general.system.Race
import net.hedtech.banner.general.system.RegulatoryRace
import net.hedtech.banner.security.BannerGrantedAuthorityService
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.banner.web.SsbLoginURLRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import grails.util.Holders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import static groovy.test.GroovyAssert.shouldFail

@Integration
@Rollback
class SurveyControllerIntegrationTests extends BaseIntegrationTestCase {
    SurveyService surveyService
    String i_success_ethnicity="1"
    String i_success_race="MOA"
    String i_success_banner_Id="HOF00720"

    String i_failure_ethnicity="02"
    String i_failure_race="MOAN"



    @Before
    public void setUp() {
        formContext = ['SELFSERVICE']
        controller = new SurveyController()
        controller.ssbLoginURLRequest = new SsbLoginURLRequest()
        controller.surveyService = surveyService
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
        logout()
    }

    private Authentication loginForRegistration(String bannerId) {
        Authentication authentication = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(bannerId, '111111'))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        return authentication

    }

    @Test
    public void testSurvey() {
        if (!isSsbEnabled()) return
        loginForRegistration(i_success_banner_Id)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        assertNotNull pidm
        Map raceMap = [:]
        List<RegulatoryRace> regulatoryRaces = RegulatoryRace.fetchRequiredRegulatoryRaces()
        regulatoryRaces.each { regulatoryRace ->
            List<Race> races = []
            races = Race.fetchAllByRegulatoryRace(regulatoryRace.code)
            if (!races.isEmpty()) {
                raceMap.put(regulatoryRace.code, races)
            }
        }
        List<PersonRace> personRaces = PersonRace.fetchByPidm(pidm)
        List<String> personRaceCodes = []
        personRaces?.each {
            personRaceCodes.add(it.race)
        }
        PersonBasicPersonBase personBasicPersonBase = PersonBasicPersonBase.fetchByPidm(pidm)
        String personEthnicity = personBasicPersonBase?.ethnic

        controller.survey()
        assertEquals controller.modelAndView.model.raceMap, raceMap
        assertEquals controller.modelAndView.model.regulatoryRaces, regulatoryRaces
        assertEquals controller.modelAndView.model.personRaceCodes, personRaceCodes
        assertEquals controller.modelAndView.model.personEthnicity, personEthnicity
        raceMap = controller.session.getAttribute("raceMap")
        regulatoryRaces = controller.session.getAttribute("regulatoryRaces")
        assertNotNull raceMap
        assertNotNull regulatoryRaces

    }



    @Test
    public void testSaveValid(){
        if (!isSsbEnabled()) return
        loginForRegistration(i_success_banner_Id)
        Integer pidm = BannerGrantedAuthorityService.getPidm()
        controller.params.ethnicity=i_success_ethnicity
        controller.params.race=i_success_race
        controller.save()
        PersonRace quiredPersonRace = PersonRace.fetchByPidmAndRace(pidm, i_success_race)
        assertNotNull quiredPersonRace

    }

    @Test
    public void testSaveInValid(){
        if (!isSsbEnabled()) return
        loginForRegistration(i_success_banner_Id)

        controller.params.ethnicity = i_failure_ethnicity
        controller.params.race = i_success_race
        shouldFail(ApplicationException) {
            controller.save()
        }
        controller.params.ethnicity = i_success_ethnicity
        controller.params.race = i_failure_race
        shouldFail(ApplicationException) {
            controller.save()
        }
        controller.params.ethnicity = i_failure_ethnicity
        controller.params.race = i_failure_race
        shouldFail(ApplicationException) {
            controller.save()
        }
    }

    @Test
    void testPathNotNull(){
        String result = controller.checkPath("/ssb/registration/save")
        assertEquals ("/ssb/registration/save",result)
        String controllerName1 = controller.checkPath("")
        assertEquals("", controllerName1)
        String controllerName2 = controller.checkPath("/ssb/userAgreement")
        assertEquals("/", controllerName2)
    }


    private def isSsbEnabled() {
        Holders.config.ssbEnabled instanceof Boolean ? Holders.config.ssbEnabled : false
    }

}