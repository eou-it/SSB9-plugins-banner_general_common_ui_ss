<%--
/*******************************************************************************
Copyright 2014-2015 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><g:message code="survey.title"/></title>
    <meta name="layout" content="bannerSelfServicePage"/>
    <meta name="menuEndPoint" content="${g.createLink(controller: 'selfServiceMenu', action: 'data')}"/>
    <meta name="menuBaseURL" content="${createLink(uri: '/ssb')}" />

    <meta name="headerAttributes" content=""/>
    <script type="text/javascript">
        document.getElementsByName('headerAttributes')[0].content = JSON.stringify({
            "pageTitle": "<g:message code="survey.edit.title" />",
            "breadcrumb": {
                "<g:message code="survey.breadcrumb.bannerSelfService"/>": "",
                "<g:message code="survey.breadcrumb.survey"/>": ""
            }
        });
    </script>

    <g:if test="${message(code: 'default.language.direction')  == 'rtl'}">
        <r:require modules="surveyRTL"/>
    </g:if>
    <g:else>
        <r:require modules="surveyLTR"/>
    </g:else>
</head>

<body>
<div id="content">
    <div id='title-panel' class='aurora-theme'></div>
    <div id="bodyContainer" class="ui-layout-center inner-center">

        <div id="pagebody" class="level4">
            <div id="contentHolder">
                <div id="contentBelt"></div>
                <div class="pagebodydiv" style="display: block;">
                    <div id="errorMessage"></div>
                    <form controller="survey" action="save" id='surveyForm' method='POST'>
                        <div id="ethnicity-race-wrapper">
                            <!--  _editSurvey.gsp-->
                            <div id="editSurvey">
                                <g:render template="editSurvey" model="${[personEthnicity: personEthnicity, regulatoryRace:regulatoryRace]}" />
                            </div>

                            <div id="confirmSurvey" role="alert">
                                <g:render template="confirmSurvey"
                                          model="${[personEthnicity: personEthnicity, regulatoryRace: regulatoryRace]}"/>
                            </div>
                            <div class="button-area">
                                <input type='button'  id="ask-me-later-btn" value= "<g:message code='survey.edit.button.askMeLater' />" class="secondary-button" data-endpoint="${createLink(controller: "survey", action: "done")}"/>
                                <input type='button'  id="save-btn" value="<g:message code='survey.edit.button.continue' />" class="primary-button" />
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
