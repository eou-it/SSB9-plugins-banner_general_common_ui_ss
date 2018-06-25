/*******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
import net.hedtech.banner.apisupport.ApiUtils
import net.hedtech.banner.overall.loginworkflow.PostLoginWorkflow
import org.grails.web.servlet.GrailsUrlPathHelper

import javax.servlet.http.HttpSession

class BannerSelfServicePostLoginFlowInterceptor {
    private static final String SLASH = "/"
    private static final String QUESTION_MARK = "?"
    def springSecurityService
    def configUserPreferenceService
    public static final String LAST_FLOW_COMPLETED = "LAST_FLOW_COMPLETED"
    private static final String USER_LOCALE_SETUP_COMPLETE = "USER_LOCALE_SETUP_COMPLETE"
    def ssbLoginURLRequest

    //def dependsOn = [net.hedtech.banner.security.AccessControlFilters.class]

    BannerSelfServicePostLoginFlowInterceptor() {
        matchAll()
                .excludes(controller: 'selfServiceMenu')
                .excludes(controller: 'login')
                .excludes(controller: 'error')
                .excludes(controller: 'dateConverter')
                .excludes(controller: 'about')
                .excludes(controller: 'theme')
                .excludes(controller: 'themeEditor')
                .excludes(controller: 'userPreference')
                .excludes(controller: 'shortcut')
                .excludes(controller: 'customPage')
                .excludes(controller: 'virtualDomainComposer')
                .excludes(controller: 'restfulApi')
                .excludes(controller: 'visualPageModelComposer')
                .excludes(controller: 'cssManager')

     }

    boolean before() {
        if (!ApiUtils.isApiRequest() && !request.xhr) {
        HttpSession session = request.getSession()

        if(session.getAttribute("maxInactiveInterval")) {
            session.setMaxInactiveInterval(session.getAttribute("maxInactiveInterval"))
            session.removeAttribute("maxInactiveInterval")
        }
        Boolean isAllFlowCompleted = new Boolean(false)
        if(session.getAttribute(PostLoginWorkflow.FLOW_COMPLETE) != null){
            isAllFlowCompleted= session.getAttribute(PostLoginWorkflow.FLOW_COMPLETE)
        }

        String path = getServletPath(request)
        if (springSecurityService.isLoggedIn() && path != null && !isAllFlowCompleted.booleanValue()) {

            log.debug "Initializing workflow classes"
            List<PostLoginWorkflow> listOfFlows = []
            listOfFlows = PostLoginWorkflow.getListOfFlows()
            Map<String, Integer> uriMap = initializeUriMap(listOfFlows)

            Integer lastFlowCompleted = session.getAttribute(LAST_FLOW_COMPLETED)


            String uriRedirected = session.getAttribute(PostLoginWorkflow.URI_REDIRECTED)

            boolean uriHampered = false
            if (uriRedirected != null) {
                String controllerRedirected = ssbLoginURLRequest.getControllerNameFromPath(uriRedirected)
                if (!path.contains(controllerRedirected)) {
                    uriHampered = true
                }
            }
            if (shouldVerifyFlowCompleted(lastFlowCompleted, path, uriMap, uriHampered)) {
                if (lastFlowCompleted == null) {
                    lastFlowCompleted = 0
                }
                session.setAttribute(PostLoginWorkflow.URI_ACCESSED, path)
                int noOfFlows = listOfFlows.size()
                for (int i = lastFlowCompleted.intValue(); i < noOfFlows; i++) {
                    session.setAttribute(LAST_FLOW_COMPLETED, new Integer(i))
                    if (listOfFlows[i].isShowPage(request)) {
                        log.debug "Workflow URI " + listOfFlows[i].getControllerUri()
                        session.setAttribute(PostLoginWorkflow.URI_REDIRECTED, listOfFlows[i].getControllerUri())
                        redirect uri: listOfFlows[i].getControllerUri()
                        return false;
                    }
                }

                session.setAttribute(PostLoginWorkflow.FLOW_COMPLETE, new Boolean(true))
            }
        }
        Boolean islocaleSetupCompleted = session.getAttribute(USER_LOCALE_SETUP_COMPLETE)
        if (springSecurityService.isLoggedIn() && path != null && !islocaleSetupCompleted) {
            def userLocale = configUserPreferenceService.getUserLocale()
            session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'] = userLocale
            log.debug "UserLocale evaluated is = "+ userLocale
            session.setAttribute(USER_LOCALE_SETUP_COMPLETE, Boolean.TRUE)
        }
    }
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }

    private boolean shouldVerifyFlowCompleted(def lastFlowCompleted, String path, HashMap<String, Integer> uriMap, boolean uriHampered) {
        return (!isFlowControllerURI(path, uriMap)) || lastFlowCompleted == null || uriHampered
    }

    public boolean isFlowControllerURI(String path, Map uriMap) {
        boolean isIgnoredUri = false;
        String controllerName = ssbLoginURLRequest.getControllerNameFromPath(path)
        if (uriMap.get(controllerName) != null) {
            isIgnoredUri = true
        }
        return isIgnoredUri
    }


    private HashMap<String, Integer> initializeUriMap(List<PostLoginWorkflow> listOfFlows) {
        HashMap<String, Integer> uriMap = new HashMap()
        int noOfFlows = listOfFlows.size()
        for (int i = 0; i < noOfFlows; i++) {
            uriMap.put(listOfFlows[i].getControllerName(), i);
        }
        return uriMap
    }

    private getServletPath(request) {
        GrailsUrlPathHelper urlPathHelper = new GrailsUrlPathHelper();
        String path = urlPathHelper.getOriginatingRequestUri(request);
        if (path != null) {
            path = path.substring(request.getContextPath().length())
            if (SLASH.equals(path)) {
                path = null
            }
            else if (request?.getQueryString()) {
                path = path + QUESTION_MARK + request?.getQueryString()
            }
        }
        return path
    }
}
