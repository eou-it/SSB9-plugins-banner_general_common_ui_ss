/*******************************************************************************
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
            angular.element(document.getElementById('dialogAppDiv')).ready(function() {
                angular.bootstrap(document.getElementById('dialogAppDiv'), ['dialogApp']);
            });
            scope = angular.element(document.getElementById('dialogAppDiv')).scope();
        }
        scope.$apply(function(){
            scope.toggleModal();
        })
    }

    $("#policy-continue").click(function () {
        var href = $(this).attr("data-endpoint");
        window.location = href;
    });
    $("#policy-exit").click(function () {
        var href = $(this).attr("data-endpoint");
        window.location = href;
    });

    $('#policy-continue').focus();
})
