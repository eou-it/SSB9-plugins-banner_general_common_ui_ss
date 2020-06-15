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

    $('#policy-continue').focus();
    document.getElementById("content").scrollTop=0;
})
