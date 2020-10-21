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
                    <div role="dialog" aria-describedby="terms-text">
                        <div class="termstextdiv">
                            <sanitizeMarkdown:renderHtml text="${infoText}" />
                        </div>
                        <div class="button-area">
                            <input aria-live="polite" type='button' value='<g:message code="net.hedtech.banner.termsofuse.button.continue"/>' id="policy-continue" class="primary"
                                   data-endpoint="${createLink(uri:'/ssb/userAgreement/agreement')}"/>

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
