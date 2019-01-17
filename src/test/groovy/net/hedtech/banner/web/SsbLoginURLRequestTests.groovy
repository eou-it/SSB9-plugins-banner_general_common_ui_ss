/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.web


class SsbLoginURLRequestTests {

    private static final String PATH_ENDS_WITH_CONTROLLER_NAME = "/ssb/registration"
    private static final String PATH_ENDS_WITH_ACTION_NAME = "/ssb/registration/save"
    private static final String PATH_WITHOUT_SSB_URL = "/securityQA/save"
    private static final String EMPTY_STRING = ""
    private static final String CONTROLLER_NAME = "registration"

    void testControllerNameFromPathEndsWithControllerName() {
        SsbLoginURLRequest ssbLoginURLRequest = new SsbLoginURLRequest()
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(PATH_ENDS_WITH_CONTROLLER_NAME)
        assertEquals(CONTROLLER_NAME, controllerName)
    }

    void testControllerNameFromPathEndsWithActionName() {
        SsbLoginURLRequest ssbLoginURLRequest = new SsbLoginURLRequest()
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(PATH_ENDS_WITH_ACTION_NAME)
        assertEquals(CONTROLLER_NAME, controllerName)
    }

    void testControllerNameFromPathWithoutSsbUrl() {
        SsbLoginURLRequest ssbLoginURLRequest = new SsbLoginURLRequest()
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(PATH_WITHOUT_SSB_URL)
        assertEquals(PATH_WITHOUT_SSB_URL, controllerName)
    }

    void testControllerNameWithEmptyPath() {
        SsbLoginURLRequest ssbLoginURLRequest = new SsbLoginURLRequest()
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(EMPTY_STRING)
        assertEquals(EMPTY_STRING, controllerName)
    }
}

