/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.general.utility

import net.hedtech.banner.security.BannerGrantedAuthorityService
import org.apache.log4j.Logger
import org.springframework.context.i18n.LocaleContextHolder

class InformationTextUtility {
    private static final log = Logger.getLogger(getClass())
    private static final SQL_ORDER_BY = " ORDER BY GURINFO_LABEL, GURINFO_SEQUENCE_NUMBER "

    /*****
     * getMessages method returns information text message for the given pagename and locale.
     * InformationTextUtility.getMessages(<GURINFO_PAGE_NAME>)
     * This utility will just take a page name and return a map of all the information texts for the specific page.
     * The map will have the label as a infoTextKey and information text as the value.
     * Example Implementation - Map infoTexts = InformationTextUtility.getMessage("TERMSELECTION"); String infoText = infoTexts.get("termSelect.bodyTitle")
     * @param pageName
     * @param locale
     * @return
     */

    public static Map getMessages(String pageName, Locale locale = LocaleContextHolder.getLocale()) {
        Map informationTexts = new HashMap<String,String>()
        Map defaultRoleInfoTexts = new HashMap<String,String>()
        String localeParam = locale.toString()
        List<String> roleCode = getQueryParamForRoles()
        List<InformationText> resultSet;
        if (roleCode) {
            resultSet = InformationText.fetchInfoTextByRoles(pageName,roleCode ,localeParam)
            resultSet = getFilteredResultSet(resultSet);
            for(InformationText infoTextsGroupByRole: resultSet) {
                String infoText =""
                if(infoTextsGroupByRole.persona == InformationTextPersonaListService.PERSONA_DEFAULT && infoTextsGroupByRole.startDate != null) {
                    infoText = defaultRoleInfoTexts.get(infoTextsGroupByRole.label)
                    infoText = infoText!=null?infoText:""
                    infoText = infoText + getInfoText(infoText, infoTextsGroupByRole)
                    defaultRoleInfoTexts.put(infoTextsGroupByRole.label, infoText)
                } else {
                    infoText = informationTexts.get(infoTextsGroupByRole.label)
                    infoText = infoText!=null?infoText:""
                    infoText = infoText + getInfoText(infoText, infoTextsGroupByRole)
                    informationTexts.put(infoTextsGroupByRole.label, infoText)
                }
            }

            defaultRoleInfoTexts.each { String infoTextKey, String value ->
                if(notExistsNonDefaultRole(informationTexts,infoTextKey)) {
                    informationTexts.put(infoTextKey, value)
                }
            }
        }
        return informationTexts
    }

    /***
     *
     * @param defaultRoleInfoTexts
     * @param informationTexts
     */
    private static boolean notExistsNonDefaultRole(Map informationTexts,String key )     {
        if (informationTexts.containsKey(key)) {
           return false
        }  else {
           return true
        }
    }

    /***
     *   Method returns a filtered result by removing baseline records if at least one local record is
     *   present for a set of labels returned by the query.
     * @param resultSet
     * @return
     */
    private static Collection<InformationText> getFilteredResultSet(List<InformationText> resultSet) {
        Set<String> labels = new HashSet<String>();
        for(InformationText row : resultSet) {
            labels.add(row.getLabel())
        }

        List<InformationText> modifiedResultSet = new ArrayList<InformationText>()
        labels.each {String label ->
            List<InformationText> resultSubSet = resultSet.findAll { row ->
                row.label == label
            }
            resultSubSet = getFilteredResultSetForLabel(resultSubSet)
            modifiedResultSet.addAll(resultSubSet)
        }
        return modifiedResultSet
    }

    /**
     * Method getQueryParamForRoles method returns roles.
     * @return
     */

    private static List<String> getQueryParamForRoles() {
        List<String> roles = BannerGrantedAuthorityService.getSelfServiceUserRole()
        roles << InformationTextPersonaListService.PERSONA_DEFAULT
        roles = getParams(roles)
        return roles
    }

    /**
     * getMessage method returns information text message for the given pagename label and locale
     * This will return the information text string for a page with specific label (infoTextKey).
     * The page name would need to be decided by respective teams to enable them to access the necessary information texts.
     * Example Implementation - def infoTexts = ["termSelect.bodyTitle": InformationTextUtility.getMessage("TERMSELECTION","termSelect.bodyTitle")]
     * @param pageName
     * @param label
     * @param locale
     * @return
     */

    public static String getMessage(String pageName, String label, Locale locale = LocaleContextHolder.getLocale()) {
        String infoText = ""
        String localeParam = locale.toString()
        List<InformationText> resultSet = InformationText.fetchInfoTextByRolesAndLabel(pageName,getQueryParamForRoles(),localeParam,label)
        resultSet = getFilteredResultSetForLabel(resultSet)
        if(resultSet.size() > 0) {
            for(InformationText infoTextResultSet : resultSet) {
                infoText += getInfoText(infoText, infoTextResultSet)
            }

            if (((infoText == null)||(infoText.trim().size()==0))) {
                infoText = label
            }
        }
        return infoText
    }

    /**
     *
     * Gives a filtered result set of a message for a particular label by removing baseline records if a local record is present.
     */
    private static Collection<InformationText> getFilteredResultSetForLabel(List<InformationText> resultSet) {
        List<InformationText> localInfoTexts = resultSet.findAll {
            it.sourceIndicator == SourceIndicators.LOCAL.getCode() && it.startDate != null
        }
        List<InformationText> baselineInfoTexts = resultSet - localInfoTexts

        if (localInfoTexts.size() > 0) {
            resultSet = getDefaultOrNonDefaultResultSet(localInfoTexts)
        }
        else {
            resultSet = getDefaultOrNonDefaultResultSet(baselineInfoTexts)
        }
        return resultSet
    }

    private static Collection<InformationText> getDefaultOrNonDefaultResultSet(List<InformationText> resultSet) {
        List<InformationText> defaultInfoText = resultSet.findAll {
            it.persona == InformationTextPersonaListService.PERSONA_DEFAULT
        }
        List<InformationText> nonDefaultInfoText = resultSet - defaultInfoText
        if(nonDefaultInfoText.size() > 0) {
           resultSet = nonDefaultInfoText
        }
        else {
           resultSet = defaultInfoText
        }
        return resultSet
    }

    private static String getInfoText(infoText, infoTextResultSet) {
        String text     =""
        String tempText = getTextBasedOnDateRange(infoTextResultSet)
        if (infoText == null || infoText == "") { text = tempText }
        else {
            if (tempText != "") { text = "\n" + tempText }
        }
        return text
    }

   private static String getTextBasedOnDateRange(InformationText row) {
        if (row.sourceIndicator == "${SourceIndicators.LOCAL.getCode()}" && row.startDate == null) {
            return ""
        }
        else {
            String text = row.text
            text = text!=null?text:""
            return text
        }
    }


    private static List<String> getParams(List<String> roles) {
        List localparams = []
        for (int i = 0; i < roles.size(); i++) {
            localparams << roles.get(i)
        }
        return localparams
    }

}
