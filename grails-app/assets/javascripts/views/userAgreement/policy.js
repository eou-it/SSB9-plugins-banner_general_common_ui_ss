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
    $('div.termstextdiv').find('blockquote')[0].id="termsText";
    document.addEventListener("keydown", function (e) {
        if(e.key == 'c' && e.altKey) {
            $("#policy-continue").focus();
            e.preventDefault();
        }
        else if(e.key == 'x'  && e.altKey) {
            $("#policy-exit").focus();
            e.preventDefault();
        }
        else if(e.key == 'i' && e.altKey) {
            $('.termstextdiv').focus();
            e.preventDefault();
        }

    });
    $('#policy-continue').focus();
    document.getElementById("content").scrollTop=0;
})
