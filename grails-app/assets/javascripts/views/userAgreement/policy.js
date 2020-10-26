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
    $('div.termstextdiv').find('blockquote')[0].id="terms-text";
    $('div.termstextdiv').find('blockquote')[0].ariaLive="assertive";
    document.addEventListener("keydown", function (e) {
        if(e.key == 'c' && e.ctrlKey) {
            console.log("c is pressed");
            $("#policy-continue").focus();
        }
        else if(e.key == 'q'  && e.ctrlKey) {
            console.log("e is pressed");
            $("#policy-exit").focus();
        }
        else if(e.key == 'm' && e.ctrlKey) {
            console.log("m is pressed");
            $('#terms-text').focus();
        }

    });
    document.getElementById("content").scrollTop=0;
})
