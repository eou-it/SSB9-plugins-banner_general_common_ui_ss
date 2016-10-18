/** *****************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
$(document).ready(function () {
    try{
        if(angular.module("aboutModal")){
            var dialogDiv = document.getElementById('dialogAppDiv');
            dialogDiv.setAttribute("ng-app","dialogApp");
            dialogDiv.setAttribute("ng-controller","ModalCtrl");
            dialogDiv.innerHTML = "<xe-about-modal show='modalShown' api='aboutApi'></xe-about-modal>";
            ToolsMenu.addItem(
                "about",
                $.i18n.prop("aurora.areas_label_about_title"),
                "",
                aboutDialogPopUp
            );
        }
    } catch(e){
       console.log('Not adding About menu item because aboutModal Module is not found in resource.');
    }
    function aboutDialogPopUp () {
        var scope = angular.element(document.getElementById('dialogAppDiv')).scope();
        if(!scope){
            setTimeout( function() {
                angular.element(document.getElementById('dialogAppDiv')).ready(function() {
                    angular.bootstrap(document.getElementById('dialogAppDiv'), ['dialogApp']);
                });
                scope = angular.element(document.getElementById('dialogAppDiv')).scope();
                scope.$apply(function(){
                    scope.toggleModal();
                })
            },5000);
        }else{
            scope.$apply(function(){
                scope.toggleModal();
            })
        }
    }
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
            _.each(notificationMessages, function (notification) {
                var n = new Notification({message:notification.message, type:"error",component: notification.component});

                notifications.addNotification(n);
            });
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
                var inputbox = $(selectElm);
                removeAriaErrors(selectElm, 'aria-invalid-question-'+j);
                $(selectElm).parent().removeClass("notification-error");
                var enteredText = $(selectElm).val();
                var invalidcharacter = $.i18n.prop("securityQA.invalid.question");
                var invalidqusetionlength = $.i18n.prop("securityQA.invalid.length.question", [questionMinimumLength]);
                $('body').append('<div role="alert" id="aria-invalid-question-'+ j + '"></div>');
                if ((enteredText.length > 0) && (enteredText.match('<') || enteredText.match('>'))) {
                    $(selectElm).parent().addClass("notification-error");

                    addAriaErrors(selectElm, invalidcharacter, "aria-invalid-question-"+j);

                    var notification = {message:invalidcharacter,component:inputbox};
                    notificationMessages.push(notification);

                }
                if (enteredText.length > 0 && enteredText.length < questionMinimumLength) {
                    $(selectElm).parent().addClass("notification-error");
                    addAriaErrors(selectElm, invalidqusetionlength, "aria-invalid-question-"+j);
                    var notification = {message:invalidqusetionlength,component:inputbox};
                    notificationMessages.push(notification);
                }
            });
        }

        $('select#question').find('option:selected').each(function (j, ielm) {

            // to clear old states
            removeAriaErrors(ielm, 'aria-invalid-select-question-'+j);

            var selectbox = $(ielm).parent();

            $(ielm).closest("div .section-wrapper").removeClass("notification-error");
            var index = parseInt($(ielm).val().substring("question".length));
            $('body').append('<div role="alert" id="aria-invalid-select-question-'+ j + '"></div>');
            if(userDefinedQuesFlag == 'N') {
                if (index == 0) {
                    var error = $.i18n.prop("securityQA.error");
                    $(ielm).closest("div .section-wrapper").addClass("notification-error");
                    addAriaErrors(ielm.parentElement, error, "aria-invalid-select-question-" + j);
                    var notification = {message:error,component:selectbox};
                    notificationMessages.push(notification);
                }
            } else {
                var userDefinedQuestion = $('input#userDefinedQuestion')[j].value;
                if (index != 0 && userDefinedQuestion.length > 0) {
                    var error = $.i18n.prop("securityQA.invalid.number.question");
                    $(ielm).closest("div .section-wrapper").addClass("notification-error");
                    addAriaErrors(ielm.parentElement, error, "aria-invalid-select-question-" + j);
                    $($('input#userDefinedQuestion')[j]).attr('aria-invalid', 'true');
                    var notification = {message:error,component:selectbox};
                    notificationMessages.push(notification);
                }
                else if (index == 0 && userDefinedQuestion.length == 0) {
                    var error = $.i18n.prop("securityQA.error");
                    $(ielm).closest("div .section-wrapper").addClass("notification-error");
                    addAriaErrors(ielm.parentElement, error, "aria-invalid-select-question-" + j);
                    $($('input#userDefinedQuestion')[j]).attr('aria-invalid', 'true');
                    var notification = {message:error,component:selectbox};
                    notificationMessages.push(notification);
                }
            }
        });

        $('input#answer').each(function (j, ielm) {

            // to clear old states
            removeAriaErrors(ielm, 'aria-invalid-answer-'+j);

            var inputbox = $(ielm);

            $(ielm).parent().removeClass("notification-error");
            $('body').append('<div role="alert" id="aria-invalid-answer-'+ j + '"></div>');
            var enteredText = $(ielm).val();
            if (enteredText.length == 0) {
                var error = $.i18n.prop("securityQA.error");
                var notification = {message:error,component:inputbox};
                notificationMessages.push(notification);
                $(ielm).parent().addClass("notification-error");
                addAriaErrors(ielm, error, "aria-invalid-answer-"+j);
            }

            if ((enteredText.length > 0) && (enteredText.match('<') || enteredText.match('>'))) {
                var invalidcharacter = $.i18n.prop("securityQA.invalid.answer");
                $(ielm).parent().addClass("notification-error");
                addAriaErrors(ielm, invalidcharacter, "aria-invalid-answer-"+j);
                var notification = {message:invalidcharacter,component:inputbox};
                notificationMessages.push(notification);
            }
            if (enteredText.length > 0 && enteredText.length < answerMinimumLength) {
                var invalidanswerlength = $.i18n.prop("securityQA.invalid.length.answer", [answerMinimumLength]);
                $(ielm).parent().addClass("notification-error");
                addAriaErrors(ielm, invalidanswerlength, "aria-invalid-answer-"+j);
                var notification = {message:invalidanswerlength,component:inputbox};
                notificationMessages.push(notification);
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
            var notification = {message:error,component:$('input#pin')};
            notificationMessages.push(notification);
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
