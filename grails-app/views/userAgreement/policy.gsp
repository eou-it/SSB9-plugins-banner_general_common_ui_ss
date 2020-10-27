<%--
/*******************************************************************************
Copyright 2014-2020 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta name="layout" content="bannerSelfServicePage"/>
    <title><g:message code="net.hedtech.banner.termsofuse.title"/></title>
    <g:if test="${message(code: 'default.language.direction')  == 'rtl'}">
        <asset:stylesheet href="modules/userAgreementRTL-mf.css"/>
    </g:if>
    <g:else>
        <asset:stylesheet href="modules/userAgreementLTR-mf.css"/>
    </g:else>
    <meta name="headerAttributes" content=""/>
        <meta name="menuBaseURL" content="${request.contextPath}/ssb"/>
    <script type="text/javascript">
        document.getElementsByName('headerAttributes')[0].content = JSON.stringify({
            "pageTitle": "<g:message code="net.hedtech.banner.termsofuse.title"/>"
        });
    </script>
</head>
<body>
<div id="content" role="main">
    <div id='title-panel' class='aurora-theme'></div>
    <div id="bodyContainer">
        <div id="pagebody" class="loginterms level">
            <div id="contentHolder">
                <div id="contentBelt"></div>
                <div class="pagebodydiv" style="display: block;">
                    <div role="dialog" aria-labelledby="termsText instructionText">
                        <div class="termstextdiv" tabindex="0" aria-hidden="true">
                            <sanitizeMarkdown:renderHtml text="${infoText}" />
                            <p id="instructionText" style="display: none;" aria-live="polite">Press ALT+C to Continue and ALT+X to exit</p>
                        </div>
                        <div class="button-area">
                            <div id="continueInstruction" style="display: none;">
                                If you agree to the terms of usage press Enter, if you want to listen to the terms of usage press ALT+I
                            </div>
                            <input type='button' value='<g:message code="net.hedtech.banner.termsofuse.button.continue"/>' id="policy-continue" class="primary"
                                   data-endpoint="${createLink(uri:'/ssb/userAgreement/agreement')}" aria-labelledby="continueInstruction"/>

                            <input type='button' value='<g:message code="net.hedtech.banner.termsofuse.button.exit"/>' id="policy-exit" class="secondary"
                                   data-endpoint="${createLink(uri:'/ssb/logout')}"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<asset:javascript src="modules/userAgreement-mf.js"/>

</body>
</html>
