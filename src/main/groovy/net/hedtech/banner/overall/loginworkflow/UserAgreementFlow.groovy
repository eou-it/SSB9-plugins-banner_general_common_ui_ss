/*******************************************************************************
 Copyright 2014-2019 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.overall.loginworkflow

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.i18n.MessageHelper
import java.sql.SQLException
import net.hedtech.banner.security.BannerGrantedAuthorityService
@Slf4j
class UserAgreementFlow extends PostLoginWorkflow {
    def sessionFactory

    public static final USER_AGREEMENT_ACTION = "useragreementdone"
    public static final TERMS_OF_USAGE_NOT_ANSWERED = "N"
    public static final TERMS_OF_USAGE_ANSWERED = "Y"
    private static final DISPLAY_STATUS = "Y"


    public boolean isShowPage(request) {
        def session = request.getSession();
        String isDone = session.getAttribute(USER_AGREEMENT_ACTION)
        boolean displayPage = false
        if (isDone != UserAgreementController.ACTION_DONE) {
            String pidm = BannerGrantedAuthorityService.getPidm()
            String displayStatus = getTermsOfUsageDisplayStatus()
            if (displayStatus?.equals(DISPLAY_STATUS)) {
                String usageIndicator = getUsageIndicator(pidm)
                if (usageIndicator?.equals(TERMS_OF_USAGE_NOT_ANSWERED)) {
                    displayPage = true
                }

            }
        }
        return displayPage
    }

    public String getControllerUri() {
        return "/ssb/userAgreement"
    }

    public String getControllerName() {
        return "userAgreement"
    }

    private String getTermsOfUsageDisplayStatus() {
        def connection
        Sql sql
        try {
            connection = sessionFactory.currentSession.connection()
            sql = new Sql(connection)
            GroovyRowResult row = sql.firstRow("""select TWGBWRUL_DISP_USAGE_IND from TWGBWRUL""")
            return row?.TWGBWRUL_DISP_USAGE_IND
        } catch (SQLException ae) {
            log.debug ae.stackTrace
            if (ae.message.contains("ORA-28000")){
                String message = MessageHelper.message("net.hedtech.banner.errors.login.locked")
                throw new ApplicationException('UserAgreementFlow', message)
            }else{
                throw ae
            }
        }
        catch (Exception ae) {
            log.debug ae.stackTrace
            throw ae
        } finally {
            //connection.close()
        }
    }

    private String getUsageIndicator(String pidm) {
        def connection
        Sql sql
        try {
            connection = sessionFactory.currentSession.connection()
            sql = new Sql(connection)
            GroovyRowResult row = sql.firstRow("""select GOBTPAC_USAGE_ACCEPT_IND from GOBTPAC where GOBTPAC_PIDM = ${pidm}""")
            return row?.GOBTPAC_USAGE_ACCEPT_IND
        } catch (SQLException ae) {
            log.debug ae.stackTrace
            throw ae
        }
        catch (Exception ae) {
            log.debug ae.stackTrace
            throw ae
        } finally {
            //connection.close()
        }
    }


}
