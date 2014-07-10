/** *****************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
$(document).ready(function () {
    $('#pin').focus();
    var ariaValid = false;

    EventDispatcher.addEventListener(Application.events.initialized, function () {
        if (window.securityQAInitErrors && window.securityQAInitErrors.notification && window.securityQAInitErrors.notification.length > 0) {

            var n = new Notification({message:window.securityQAInitErrors.notification, type:"error"});
            notifications.addNotification(n);

            $('body').append('<div role="alert" id="server-error" class="hide-aria-message">' + window.securityQAInitErrors.notification + '</div>');
        }
    });
    var notificationMessages = new Array();

    $('select#question').each(function (j, ielm) {
        $($(ielm).find('option[value="' + selectedQues[j] + '"]')).selected();
    });

    $("#security-save-btn").click(function () {
        notificationMessages = [];
        validateForm();
        if (notificationMessages && notificationMessages.length > 0) {
            _.each(notificationMessages, function (message) {
                var n = new Notification({message:message, type:"error"});

                notifications.addNotification(n);
            });

            var n = new Notification( {message: $.i18n.prop("js.notification.dirtyCheck.message"), type:"warning", promptMessage: $.i18n.prop("js.notification.dirtyCheck.promptMessage")} );

            n.addPromptAction( $.i18n.prop("js.notification.dirtyCheck.cancelActionButton"), function() {
                notifications.remove( n );
            });

            n.addPromptAction( $.i18n.prop("js.notification.dirtyCheck.doNotSaveActionButton"), function() {
                notifications.remove( n );
            });

            n.addPromptAction( $.i18n.prop("js.notification.dirtyCheck.saveActionButton"), function() {
                notifications.remove( n );
                var form = document.getElementById('securityForm');
                form.submit();

            });

            notifications.addNotification( n );

            return false;
        }
        else {
            var form = document.getElementById('securityForm');
            form.submit();
        }
    });

    function validateForm() {
        notifications.clearNotifications();
        validatePin();


        if(userDefinedQuesFlag == 'Y') {
            $('input#userDefinedQuestion').each(function (j, selectElm) {
                // to clear old states
                removeAriaErrors(selectElm, 'aria-invalid-question-'+j);
                $(selectElm).parent().removeClass("notification-error");
                var enteredText = $(selectElm).val();
                var invalidcharacter = $.i18n.prop("securityQA.invalid.question");
                var invalidqusetionlength = $.i18n.prop("securityQA.invalid.length.question", [questionMinimumLength]);
                $('body').append('<div role="alert" id="aria-invalid-question-'+ j + '"></div>');
                if ((enteredText.length > 0) && (enteredText.match('<') || enteredText.match('>'))) {
                    $(selectElm).parent().addClass("notification-error");

                    addAriaErrors(selectElm, invalidcharacter, "aria-invalid-question-"+j);

                    notificationMessages.push(invalidcharacter);
                }
                if (enteredText.length > 0 && enteredText.length < questionMinimumLength) {
                    $(selectElm).parent().addClass("notification-error");
                    addAriaErrors(selectElm, invalidqusetionlength, "aria-invalid-question-"+j);
                    notificationMessages.push(invalidqusetionlength);
                }
            });
        }

        $('select#question').find('option:selected').each(function (j, ielm) {

            // to clear old states
            removeAriaErrors(ielm, 'aria-invalid-select-question-'+j);

            $(ielm).closest("div .section-wrapper").removeClass("notification-error");
            var index = parseInt($(ielm).val().substring("question".length));
            $('body').append('<div role="alert" id="aria-invalid-select-question-'+ j + '"></div>');
            if(userDefinedQuesFlag == 'N') {
                if (index == 0) {
                    var error = $.i18n.prop("securityQA.error");
                    $(ielm).closest("div .section-wrapper").addClass("notification-error");
                    addAriaErrors(ielm.parentElement, error, "aria-invalid-select-question-" + j);
                    notificationMessages.push(error);
                }
            } else {
                var userDefinedQuestion = $('input#userDefinedQuestion')[j].value;
                if (index != 0 && userDefinedQuestion.length > 0) {
                    var error = $.i18n.prop("securityQA.invalid.number.question");
                    $(ielm).closest("div .section-wrapper").addClass("notification-error");
                    addAriaErrors(ielm.parentElement, error, "aria-invalid-select-question-" + j);
                    $($('input#userDefinedQuestion')[j]).attr('aria-invalid', 'true');
                    notificationMessages.push(error);
                }
                else if (index == 0 && userDefinedQuestion.length == 0) {
                    var error = $.i18n.prop("securityQA.error");
                    $(ielm).closest("div .section-wrapper").addClass("notification-error");
                    addAriaErrors(ielm.parentElement, error, "aria-invalid-select-question-" + j);
                    $($('input#userDefinedQuestion')[j]).attr('aria-invalid', 'true');
                    notificationMessages.push(error);
                }
            }
        });

        $('input#answer').each(function (j, ielm) {

            // to clear old states
            removeAriaErrors(ielm, 'aria-invalid-answer-'+j);

            $(ielm).parent().removeClass("notification-error");
            $('body').append('<div role="alert" id="aria-invalid-answer-'+ j + '"></div>');
            var enteredText = $(ielm).val();
            if (enteredText.length == 0) {
                var error = $.i18n.prop("securityQA.error");
                notificationMessages.push(error);
                $(ielm).parent().addClass("notification-error");
                addAriaErrors(ielm, error, "aria-invalid-answer-"+j);
            }

            if ((enteredText.length > 0) && (enteredText.match('<') || enteredText.match('>'))) {
                var invalidcharacter = $.i18n.prop("securityQA.invalid.answer");
                $(ielm).parent().addClass("notification-error");
                addAriaErrors(ielm, invalidcharacter, "aria-invalid-answer-"+j);
                notificationMessages.push(invalidcharacter);
            }
            if (enteredText.length > 0 && enteredText.length < answerMinimumLength) {
                var invalidanswerlength = $.i18n.prop("securityQA.invalid.length.answer", [answerMinimumLength]);
                $(ielm).parent().addClass("notification-error");
                addAriaErrors(ielm, invalidanswerlength, "aria-invalid-answer-"+j);
                notificationMessages.push(invalidanswerlength);
            }
        });
    }

    function addAriaErrors(ielm, error, id) {

        setAriaInvalidTrueAndDescribedByError(ielm, id);
        $('#' + id).append('<p class="hide-aria-message">' + error + '</p>');
    }

    function removeAriaErrors(ielm, id) {
        setAriaInvalidFalseAndRemoveDescribedByError(ielm);
        $('#'+id).remove();
    }

    function setAriaInvalidTrueAndDescribedByError(ielm, id) {
        $(ielm).attr('aria-invalid', 'true');
        $(ielm).attr('aria-describedby', id);
    }

    function setAriaInvalidFalseAndRemoveDescribedByError(ielm) {
        $(ielm).attr('aria-invalid', 'false');
        $(ielm).attr('aria-describedby', '');
    }

    $("#security-cancel-btn").click(function () {
        var href = $(this).attr("data-endpoint")
        window.location = href;
    });


    $('select#question').live('change', function () {
        updateSelects();
    });

    function updateSelects() {
        $('select#question').each(
            function (j, elem) {
                var $selected = $(elem).find("option:selected");
                var $opts = $("<div>");

                var index = parseInt($($selected).val().substring("question".length));
                if (index != 0) {
                    $($selected).parent().removeClass("notification-error");
                }

                var newArray = new Array();
                for (var i = 0; i < questions.length; i++) {
                    newArray.push(questions[i]);
                }

                $('select#question').find('option:selected').each(function (j, ielm) {
                    var index = parseInt($(ielm).val().substring("question".length));
                    if (elem != $(ielm).parent()[0]) {
                        newArray[index] = "";
                    }

                });

                $opts.append('<option value=question0>' + questions[0] + '</option>');
                for (var i = 1; i < newArray.length; i++) {
                    if (newArray[i] != "") {
                        $opts.append('<option value=question' + i + '>' + questions[i] + '</option>');
                    }
                }

                $(elem).html($opts.html());
                if ($selected.length) {
                    $(elem).val($selected.val());
                }
            });
    }


    function validatePin() {
        var error = $.i18n.prop("securityQA.invaild.pin");
        var blankPinAnswerNotification = new Notification({message:error, type:"error"});
        if ($('input#pin').val().length == 0) {
            notificationMessages.push(error);
            $('input#pin').parent().addClass("notification-error");
            setAriaInvalidTrueAndDescribedByError('input#pin', 'invalid-pin');
            $('#invalid-pin').remove();
            $('body').append('<div role="alert" id="invalid-pin" class="hide-aria-message">' + error + '</div>');
        } else {
            notificationMessages.splice(notificationMessages.indexOf(error));
            $('input#pin').parent().removeClass("notification-error");
            setAriaInvalidFalseAndRemoveDescribedByError('input#pin')
            $('#invalid-pin').remove();
        }
    }
})
