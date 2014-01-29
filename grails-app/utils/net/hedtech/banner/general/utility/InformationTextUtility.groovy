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

    public static Map getMessages(String pageName, Locale locale = LocaleContextHolder.getLocale()) {
        Map informationTexts = new HashMap()
        Map defaultRoleInfoTexts = new HashMap()
        def sql = null
        try {
            String localeParam = locale.toString();
            List<String> roles = BannerGrantedAuthorityService.getSelfServiceUserRole()
            roles << InformationTextPersonaListService.PERSONA_DEFAULT
            if (roles) {
                List<String> params = [pageName]
                String roleClauseParams = getQueryPlaceHolders(roles)
                List<String> temporaryParams = params
                temporaryParams.addAll(getParams(roles))
                temporaryParams << localeParam
                sql = getSQLObject()
                def queryString = " ${buildBasicQueryString(roleClauseParams)} ORDER BY GURINFO_LABEL, GURINFO_SEQUENCE_NUMBER "
                String sqlQueryString = queryString.toString()
                def resultSet = sql.rows(sqlQueryString, temporaryParams)
                resultSet = getFilteredResultSet(resultSet);

                resultSet.each { t ->
                    if(t.GURINFO_ROLE_CODE == InformationTextPersonaListService.PERSONA_DEFAULT && t.GURINFO_START_DATE != null) {
                        String infoText = defaultRoleInfoTexts.get(t.GURINFO_LABEL)
                        infoText = getInfoText(infoText, t)
                        defaultRoleInfoTexts.put(t.GURINFO_LABEL, infoText)
                    } else {
                        String infoText = informationTexts.get(t.GURINFO_LABEL)
                        infoText = getInfoText(infoText, t)
                        informationTexts.put(t.GURINFO_LABEL, infoText)
                    }
                }

                defaultRoleInfoTexts.each {key, value ->
                    if(!informationTexts.containsKey(key)) {
                        informationTexts.put(key, value)
                    }
                }
            }
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
        return informationTexts
    }

    private static Collection<GroovyRowResult> getFilteredResultSet(List<GroovyRowResult> resultSet) {
        Set<String> labels = new HashSet<String>();
        resultSet.each { row ->
            labels.add(row.GURINFO_LABEL)
        }

        List<GroovyRowResult> modifiedResultSet = new ArrayList<GroovyRowResult>()
        labels.each { label ->
            List<GroovyRowResult> resultSubSet = resultSet.findAll { row ->
                row.GURINFO_LABEL == label
            }
            resultSubSet = getFilteredResultSetForLabel(resultSubSet)
            modifiedResultSet.addAll(resultSubSet)
        }
        return modifiedResultSet
    }



    public static String getMessage(String pageName, String label, Locale locale = LocaleContextHolder.getLocale()) {
        String infoText = null
        Sql sql = null
        try {
            String localeParam = locale.toString();
            List<String> roles = BannerGrantedAuthorityService.getSelfServiceUserRole()
            roles << InformationTextPersonaListService.PERSONA_DEFAULT
            if (roles) {
                List<String> params = [pageName]
                String roleClauseParams = getQueryPlaceHolders(roles)
                List<String> temporaryParams = params
                temporaryParams.addAll(getParams(roles))
                temporaryParams << localeParam
                temporaryParams << label
                def queryString = " ${buildBasicQueryString(roleClauseParams)} AND GURINFO_LABEL = ?   ORDER BY GURINFO_LABEL, GURINFO_SEQUENCE_NUMBER "
                String sqlQueryString = queryString.toString()
                sql = getSQLObject()
                def resultSet = sql.rows(sqlQueryString, temporaryParams)

                resultSet = getFilteredResultSetForLabel(resultSet)

                resultSet.each {t ->
                    infoText = getInfoText(infoText, t)
                }
            }
            if (infoText == null) {
                infoText = label
            }
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
        return infoText
    }

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
        resultSet
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

    private static String getInfoText(infoText, t) {
        if (infoText == null || infoText == "") { infoText = getTextBasedOnDateRange(t) }
        else {
            if (getTextBasedOnDateRange(t) != "") { infoText += "\n" + getTextBasedOnDateRange(t) }
        }
        return infoText
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
