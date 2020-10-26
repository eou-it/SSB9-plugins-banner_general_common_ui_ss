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
    document.addEventListener("keydown", function (e) {
        if(e.key == 'c' && e.altKey) {
            $("#policy-continue").focus();
        }
        else if(e.key == 'x'  && e.altKey) {
            $("#policy-exit").focus();
        }
        else if(e.key == 'i' && e.altKey) {
            $('#terms-text').focus();
        }

    });
    $('#policy-continue').focus();
    document.getElementById("content").scrollTop=0;
})
