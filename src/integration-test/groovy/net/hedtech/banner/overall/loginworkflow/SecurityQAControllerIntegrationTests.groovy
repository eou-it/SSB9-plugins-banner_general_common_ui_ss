/*******************************************************************************
 Copyright 2014-2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.overall.loginworkflow
import org.junit.Before
import org.junit.Test
import org.junit.After
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import groovy.sql.Sql
import net.hedtech.banner.testing.BaseIntegrationTestCase
import grails.util.Holders
import grails.web.context.ServletContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken as UPAT
import org.springframework.security.core.context.SecurityContextHolder
import net.hedtech.banner.general.overall.PinQuestion

@Integration
@Rollback
class SecurityQAControllerIntegrationTests extends BaseIntegrationTestCase {

    def selfServiceBannerAuthenticationProvider
    def securityQAService
    def noOfQuestions
    def questions = [:]
    List dataToView = []
    def questionMinimumLength
    def answerMinimumLength
    def userDefinedQuesFlag
    def questionList = []
    def selectedQues = []
    def i_user_question_id ="question0"
    def i_user_question = "My First school"
    def i_question1 = "Fav destination?"
    def i_question2 = "Fav food?"
    def pidm = 400720

	@Before
	public void setUp() {

        // For testing RESTful APIs, we don't want the default 'controller support' added by our base class.
        // Most importantly, we don't want to redefine the controller's params to be a map within this test,
        // as we need Grails to automatically populate the params from the request.
        //
        // So, we'll set the formContext and then call super(), just as if this were not a controller test.
        // That is, we'll set the controller after we call super() so the base class won't manipulate it.
        if (!isSsbEnabled()) return
        formContext = ['GUAGMNU']

        controller = new SecurityQAController()

        super.setUp()
        ServletContextHolder.servletContext.removeAttribute("gtvsdax")
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UPAT('HOF00720', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
    }


	@After
	public void tearDown() {
        if (!isSsbEnabled()) return
        super.tearDown()
        logout()
    }

	@Test
    void testRetrieveDate() {
        if (!isSsbEnabled()) return
        controller.index()
        assertEquals controller.response.status, 200
        def securityQAData = controller?.response?.contentAsString
        assertNotNull securityQAData
        def ques = PinQuestion.fetchQuestions()
        userDefinedQuesFlag = securityQAService.getUserDefinedPreference().GUBPPRF_EDITQSTN_IND
        ques.each {
            questions.put(it.pinQuestionId, it.description)
        }
       questions.values().collect()
        noOfQuestions = securityQAService.getUserDefinedPreference().GUBPPRF_NO_OF_QSTNS
        questionMinimumLength = securityQAService.getUserDefinedPreference().GUBPPRF_QSTN_MIN_LENGTH
        answerMinimumLength = securityQAService.getUserDefinedPreference().GUBPPRF_ANSR_MIN_LENGTH
        assertTrue !controller?.response?.contentAsString?.equals("[]")
        def fields = renderMap.model
        assertEquals fields.questionMinimumLength, questionMinimumLength, 0
        assertEquals fields.answerMinimumLength, answerMinimumLength, 0
        assertEquals fields.questions.size(), ques.size(), 0
    }

	@Test
    void testSave(){
        if (!isSsbEnabled()) return
        def pinQuestion1  = newValidForCreatePinQuestion("TT11" ,i_question1 )
        pinQuestion1.save( failOnError: true, flush: true )
        assertNotNull pinQuestion1.id

        def pinQuestion2  = newValidForCreatePinQuestion("TT12",i_question2)
        pinQuestion2.save( failOnError: true, flush: true )
        assertNotNull pinQuestion2.id

        List questionList = []
        def ques = PinQuestion.fetchQuestions()
        Map questions = [:]
        ques.each {
            questions.put(it.pinQuestionId, it.description)
        }
        questionList = questions.values().collect()
        setNumberOfQuestion(3)
        assertEquals 3, securityQAService.getUserDefinedPreference().GUBPPRF_NO_OF_QSTNS, 0

        int question1Index = questionList.indexOf(pinQuestion1.getDescription())+1
        int question2Index = questionList.indexOf(pinQuestion2.getDescription()) +1
        String question1 = "question"+question1Index
        String question2 = "question"+question2Index

        controller.params.question = [question1,question2,"question0"]
        controller.params.userDefinedQuestion = ['','',i_user_question]
        controller.params.answer=["answer1","answer2","answer3"]
        controller.params.pin='111111'

        controller.save()
        assertEquals controller.response.status, 200
        def securityQAData = controller?.response?.contentAsString
        int ansrCount = securityQAService.getNumberOfQuestionsAnswered(pidm)
        assertEquals 3, ansrCount, 0
        assertNotNull securityQAData

    }

    private void setNumberOfQuestion(int noOfQuestions){
        def sql
        try {
            sql = new Sql(sessionFactory.getCurrentSession().connection())
            sql.executeUpdate("update GUBPPRF set GUBPPRF_NO_OF_QSTNS = ?",[noOfQuestions])
        } finally {
            sql?.close() // note that the test will close the connection, since it's our current session's connection
        }
    }

    private def newValidForCreatePinQuestion(String pinQuestionId,String desc) {
        def pinQuestion = new PinQuestion(
                pinQuestionId: pinQuestionId,
                description: desc,
                displayIndicator: true,
        )
        return pinQuestion
    }

    private def isSsbEnabled() {
        Holders.config.ssbEnabled instanceof Boolean ? Holders.config.ssbEnabled : false
    }

    @Test
    void testinvalidQuestionerrorMessage() {
        def msg = controller.getErrorMessage("securityQA.invalid.length.question")
        assertEquals(msg, "Question has to be 0 characters or more")
    }

    @Test
    void testinvalidAnswererrorMessage() {
        def msg = controller.getErrorMessage("securityQA.invalid.length.answer")
        assertEquals(msg, "Answer has to be 0 characters or more")
    }
}
