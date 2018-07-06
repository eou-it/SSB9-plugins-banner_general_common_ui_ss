<%--
/*******************************************************************************
Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><g:message code="net.hedtech.banner.termsofuse.title"/></title>
    <meta name="layout" content="bannerSelfServicePage"/>

    <g:if test="${message(code: 'default.language.direction')  == 'rtl'}">
        <asset:javascript src="modules/userAgreementRTL-mf.js"/>
        <asset:stylesheet href="modules/userAgreementRTL-mf.css"/>
    </g:if>
    <g:else>
        <asset:javascript src="modules/userAgreementLTR-mf.js"/>
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
<div id="content">
    <div id='title-panel' class='aurora-theme'></div>
    <div id="bodyContainer">
        <div id="pagebody" class="loginterms level">
            <div id="contentHolder">
                <div id="contentBelt"></div>
                <div class="pagebodydiv" style="display: block;">
                    <div role="dialog" aria-describedby="terms-text-style">
                        <div class="termstextdiv">
                            <sanitizeMarkdown:renderHtml text="${infoText}" />
                        </div>
                        <div class="button-area">
                            <input type='button' value='<g:message code="net.hedtech.banner.termsofuse.button.continue"/>' id="policy-continue" class="primary"
                                   data-endpoint="${createLink(controller: "userAgreement", action: "agreement")}"/>
                            <input type='button' value='<g:message code="net.hedtech.banner.termsofuse.button.exit"/>' id="policy-exit" class="secondary"
                                   data-endpoint="${createLink(controller: "logout")}"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
