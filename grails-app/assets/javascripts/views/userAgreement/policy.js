/*******************************************************************************
Copyright 2016-2020 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

$(document).ready(function () {
    $("#policy-continue").click(function () {
        var href = $(this).attr("data-endpoint");
        window.location = href;
    });
    $("#policy-exit").click(function () {
        var href = $(this).attr("data-endpoint");
        window.location = href;
    });
    document.getElementsByClassName('termstextdiv')[0].children[0].childNodes[0].id="userAgreementContent"
    document.getElementsByClassName('termstextdiv')[0].children[0].childNodes[1].id="userAgreementInstruction"
    $('#policy-continue').focus();
    document.getElementById("content").scrollTop=0;
})
