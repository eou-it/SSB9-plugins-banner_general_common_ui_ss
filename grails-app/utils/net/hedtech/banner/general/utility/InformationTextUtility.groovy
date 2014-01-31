/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.general.utility

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import net.hedtech.banner.security.BannerGrantedAuthorityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.i18n.LocaleContextHolder
import java.sql.SQLException

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
        List<String> roles = BannerGrantedAuthorityService.getSelfServiceUserRole()
        roles << InformationTextPersonaListService.PERSONA_DEFAULT
        if (roles) {
            List<String> temporaryParams = buildQueryParams(pageName,locale)
            def resultSet = executeQuery(temporaryParams, SQL_ORDER_BY)
            resultSet = getFilteredResultSet(resultSet);
            resultSet.each { GroovyRowResult infoTextsGroupByRole ->
                if(infoTextsGroupByRole.GURINFO_ROLE_CODE == InformationTextPersonaListService.PERSONA_DEFAULT && infoTextsGroupByRole.GURINFO_START_DATE != null) {
                    String infoText = defaultRoleInfoTexts.get(infoTextsGroupByRole.GURINFO_LABEL)
                    infoText = getInfoText(infoText, infoTextsGroupByRole)
                    defaultRoleInfoTexts.put(infoTextsGroupByRole.GURINFO_LABEL, infoText)
                } else {
                    String infoText = informationTexts.get(infoTextsGroupByRole.GURINFO_LABEL)
                    infoText = getInfoText(infoText, infoTextsGroupByRole)
                    informationTexts.put(infoTextsGroupByRole.GURINFO_LABEL, infoText)
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
     * executeQuery method is just to execute the query to fetch the
     * info text with the given params.
     * @param queryParams
     * @param sqlQueryString
     * @return
     */
    private static List<GroovyRowResult> executeQuery(List<String> queryParams, String sqlQueryString)    {
        def resultSet = null
        Sql sql = null
        try {
            List<String> roles = BannerGrantedAuthorityService.getSelfServiceUserRole()
            roles << InformationTextPersonaListService.PERSONA_DEFAULT
            String roleClauseParams = getQueryPlaceHolders(roles)
            StringBuffer sb = new StringBuffer( ) ;
            def queryString =  " ${buildBasicQueryString(roleClauseParams)}  "
            sb.append(queryString.toString())
            sb.append(sqlQueryString)
            sqlQueryString = sb.toString()
            sql = getSQLObject()
            resultSet = sql.rows(sqlQueryString, queryParams)
        } finally {
            try {
                if (sql) {
                    sql.close()
                }
            } catch (SQLException ae) {
                log.debug ae.stackTrace
                throw ae
            }
        }
        return resultSet
    }


    /***
     * buildQueryParams method is just to prepare the query params
     * for the getMessage and getMessages API. Params which are common between these two apis
     * are extracted and put it in a method. If additional params are required those are added in the
     * respective methods itself.
     * @param pageName
     * @param locale
     * @return
     */

    private static List<String> buildQueryParams(String pageName, Locale locale) {
        String localeParam = locale.toString();
        List<String> roles = BannerGrantedAuthorityService.getSelfServiceUserRole()
        roles << InformationTextPersonaListService.PERSONA_DEFAULT
        List<String> params = [pageName]
        List<String> temporaryParams = params
        temporaryParams.addAll(getParams(roles))
        temporaryParams << localeParam
        return temporaryParams
    }

    /***
     *   Method returns a filtered result by removing baseline records if at least one local record is
     *   present for a set of labels returned by the query.
     * @param resultSet
     * @return
     */
    private static Collection<GroovyRowResult> getFilteredResultSet(List<GroovyRowResult> resultSet) {
        Set<String> labels = new HashSet<String>();
        resultSet.each { GroovyRowResult row ->
            labels.add(row.GURINFO_LABEL)
        }

        List<GroovyRowResult> modifiedResultSet = new ArrayList<GroovyRowResult>()
        labels.each {String label ->
            List<GroovyRowResult> resultSubSet = resultSet.findAll { row ->
                row.GURINFO_LABEL == label
            }
            resultSubSet = getFilteredResultSetForLabel(resultSubSet)
            modifiedResultSet.addAll(resultSubSet)
        }
        return modifiedResultSet
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
        def temporaryParams = buildQueryParams(pageName,locale)
        temporaryParams << label
        StringBuffer sbSqlWhereClause = new StringBuffer()
        sbSqlWhereClause.append(" AND GURINFO_LABEL = ?  ")
        sbSqlWhereClause.append(SQL_ORDER_BY)
        def resultSet = executeQuery(temporaryParams,sbSqlWhereClause.toString());
        resultSet = getFilteredResultSetForLabel(resultSet)
        resultSet.each {GroovyRowResult infoTextResultSet ->
            infoText += getInfoText(infoText, infoTextResultSet)
        }

        if (infoText == null) {
            infoText = label
        }
        return infoText
    }

    /**
     *
     * Gives a filtered result set of a message for a particular label by removing baseline records if a local record is present.
     */
    private static Collection<GroovyRowResult> getFilteredResultSetForLabel(List<GroovyRowResult> resultSet) {
        List<GroovyRowResult> localInfoTexts = resultSet.findAll {
            it.GURINFO_SOURCE_INDICATOR == SourceIndicators.LOCAL.getCode() && it.GURINFO_START_DATE != null
        }
        List<GroovyRowResult> baselineInfoTexts = resultSet - localInfoTexts

        if (localInfoTexts.size() > 0) {
            resultSet = getDefaultOrNonDefaultResultSet(localInfoTexts)
        }
        else {
            resultSet = getDefaultOrNonDefaultResultSet(baselineInfoTexts)
        }
        return resultSet
    }

    private static Collection<GroovyRowResult> getDefaultOrNonDefaultResultSet(List<GroovyRowResult> resultSet) {
        List<GroovyRowResult> defaultInfoText = resultSet.findAll {
            it.GURINFO_ROLE_CODE == InformationTextPersonaListService.PERSONA_DEFAULT
        }
        List<GroovyRowResult> nonDefaultInfoText = resultSet - defaultInfoText
        if(nonDefaultInfoText.size() > 0) {
            resultSet = nonDefaultInfoText
        }
        else {
            resultSet = defaultInfoText
        }
    }

    private static String getInfoText(infoText, infoTextResultSet) {
        String text     =""
        if (infoText == null || infoText == "") { text = getTextBasedOnDateRange(infoTextResultSet) }
        else {
            if (getTextBasedOnDateRange(infoTextResultSet) != "") { text = "\n" + getTextBasedOnDateRange(infoTextResultSet) }
        }
        return text
    }

    private static String buildBasicQueryString(roleClauseParams) {
        return """ SELECT * FROM gurinfo a
                           WHERE gurinfo_page_name = ?
                           AND GURINFO_ROLE_CODE IN (${roleClauseParams})
                           AND GURINFO_LOCALE = ?
                           AND GURINFO_SOURCE_INDICATOR =
                           (
                               SELECT nvl( MAX(GURINFO_SOURCE_INDICATOR ),'${SourceIndicators.BASELINE.getCode()}')
                               FROM GURINFO
                               WHERE gurinfo_page_name = a.gurinfo_page_name
                               AND GURINFO_LABEL = a.GURINFO_LABEL
                               AND GURINFO_SEQUENCE_NUMBER = a.GURINFO_SEQUENCE_NUMBER
                               AND GURINFO_ROLE_CODE = a.GURINFO_ROLE_CODE
                               AND GURINFO_LOCALE = a.GURINFO_LOCALE
                               AND GURINFO_SOURCE_INDICATOR ='${SourceIndicators.LOCAL.getCode()}'
                               AND TRUNC(SYSDATE) BETWEEN TRUNC(NVL( GURINFO_START_DATE, (SYSDATE - 1) ) ) AND TRUNC( NVL( GURINFO_END_DATE, (SYSDATE + 1) ))
                           )"""


    }

    private static String getTextBasedOnDateRange(row) {
        if (row.GURINFO_SOURCE_INDICATOR == "${SourceIndicators.LOCAL.getCode()}" && row.GURINFO_START_DATE == null) {
            return ""
        }
        else {
            row.GURINFO_TEXT
        }
    }

    private static Sql getSQLObject() {
        def ctx = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
        def sessionFactory = ctx.sessionFactory
        def session = sessionFactory.currentSession
        def sql = new Sql(session.connection())
        sql
    }

    private static List getParams(List<String> roles) {
        List localparams = []
        for (int i = 0; i < roles.size(); i++) {
            localparams << roles.get(i)
        }
        return localparams
    }

    private static String getQueryPlaceHolders(List<String> roles) {
        StringBuilder roleClauseParams = new StringBuilder()
        if (roles.size() >= 1) {
            roleClauseParams.append("?")
        }
        for (int i = 1; i < roles.size(); i++) {
            roleClauseParams.append(",?")
        }
        roleClauseParams.toString()
    }
}
