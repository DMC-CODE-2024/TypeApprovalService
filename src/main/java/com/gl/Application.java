package com.gl;

import com.gl.Audit.ModulesAudit;
import com.gl.Config.MySQLConnection;
import com.gl.Config.PropertyReader;
import com.gl.P5Process.Process;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    public static PropertyReader propertyReader = new PropertyReader();
    static Logger log = LogManager.getLogger(Application.class);
    public static String audDb;
    public static String serverName;

    public static void main(String[] args) {
        int auditId = 0;
        long startTime = System.currentTimeMillis();
        try {
            audDb = propertyReader.getConfigPropValue("auddbName");
            serverName = propertyReader.getConfigPropValue("serverName");
        } catch (Exception e) {
            log.error("Not able to fetch information for serverName " + e);
        }
        try (var c = new MySQLConnection().getConnection()) {

            Process.p5(c);

        } catch (Exception e) {
            log.error("Error in Service  " + e);
            try (var c = new MySQLConnection().getConnection()) {
                long endTime = System.currentTimeMillis();
                ModulesAudit.updateModuleAudit(c, 500, "FAILURE", e.getMessage(), auditId, startTime, 0, 0);
            } catch (Exception ex) {
                log.error("Error updating audit trail " + ex);
            }
        }
        System.exit(0);
    }
}