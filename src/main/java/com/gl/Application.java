package com.gl;

import com.gl.Audit.ModulesAudit;
import com.gl.Config.MySQLConnection;
import com.gl.Config.PropertyReader;
import com.gl.P5Process.Process;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gl.P5Process.QueryExecuter;

import java.sql.Connection;

public class Application {
    public static PropertyReader propertyReader = new PropertyReader();
    static Logger log = LogManager.getLogger(Application.class);
    public static String audDb;
    public static String serverName;
    public static int totalcount;

    public static void main(String[] args) {
        int auditId = 0;
        long startTime = System.currentTimeMillis();
        try {
            audDb = propertyReader.getConfigPropValue("auddbName");
            serverName = propertyReader.getConfigPropValue("serverName");
            totalcount=0;
        } catch (Exception e) {
            log.error("Not able to fetch information for serverName " + e);
        }
        try (var c = new MySQLConnection().getConnection()) {
            auditId = ModulesAudit.insertModuleAudit(c, "type_approval_module", "Process for type_approval_module", serverName);
            Process.p5(c, auditId, startTime);
        } catch (Exception e) {
            log.error("Error in Service  " + e);
            try (var c = new MySQLConnection().getConnection()) {
                long endTime = System.currentTimeMillis();
                int totalFileCount = getTotalFileCount(c);
                ModulesAudit.updateModuleAudit(c, 500, "FAILURE", e.getMessage(), auditId, startTime, totalFileCount, totalcount);
            } catch (Exception ex) {
                log.error("Error updating audit trail " + ex);
            }
        }
        System.exit(0);
    }

    private static int getTotalFileCount(Connection conn) {
        String query = "SELECT COUNT(*) FROM app.national_whitelist";
        return QueryExecuter.runQuery(conn, query);
    }
}