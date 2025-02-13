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
    public static int passcount;
    public static int failcount;

    public static void main(String[] args) {
        try {
            audDb = propertyReader.getConfigPropValue("auddbName");
            serverName = propertyReader.getConfigPropValue("serverName");
            passcount = 0;
            failcount = 0;
        } catch (Exception e) {
            log.error("Not able to fetch information for serverName " + e);
        }
        try (var c = new MySQLConnection().getConnection()) {
            int auditId = ModulesAudit.insertModuleAudit(c, "Type Approval");
            Process.p5(c);
            ModulesAudit.updateModuleAudit(c, 1, passcount, failcount, auditId);
        } catch (Exception e) {
            log.error("Error in Service  " + e);
        }
        System.exit(0);
    }
}