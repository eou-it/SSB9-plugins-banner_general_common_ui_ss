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
    var termsOfUsageShortcuts = [
        'alt+c', function() {
            $("#policy-continue").focus();
        },
        'alt+x', function() {
            $("#policy-exit").focus();
        },
        'alt+i', function() {
            $('.termstextdiv').focus();
        }
    ];
    key && key.bind.apply( window, termsOfUsageShortcuts );

    $('#policy-continue').focus();
    document.getElementById("content").scrollTop=0;
})
