package com.gl.Audit;

import com.gl.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

public class ModulesAudit {
    static Logger logger = LogManager.getLogger(ModulesAudit.class);

    public static int insertModuleAudit(Connection conn, String featureName, String processName, String servername) {
        int generatedKey = 0;
        String query = "INSERT INTO " + Application.audDb + ".modules_audit_trail " +
                "(status_code, status, feature_name, info, count2, action, server_name, execution_time, module_name, failure_count) " +
                "VALUES('201', 'INITIAL', '" + featureName + "', '" + processName + "', '0', '', '" + servername + "', '0', 'Type Approval', '0')";
        logger.info(query);
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("Unable to insert module audit: " + e.getLocalizedMessage());
        }
        return generatedKey;
    }

    public static void updateModuleAudit(Connection conn, int statusCode, String status, String errorMessage, int id, long executionStartTime, long numberOfRecord, long totalFileCount) {
        String exec_time = " TIMEDIFF(now(), created_on) ";
        if (conn.toString().contains("oracle")) {
            long milliseconds = (new Date().getTime()) - executionStartTime;
            String executionFinishTime = (((milliseconds / 1000) / 60) / 60) + ":" + (((milliseconds / 1000) / 60) % 60) + ":" + ((milliseconds / 1000) % 60);
            exec_time = " '" + executionFinishTime + "' ";
        }
        try (Statement stmt = conn.createStatement()) {
            String query = "UPDATE " + Application.audDb + ".modules_audit_trail SET " +
                    "status_code='" + statusCode + "', status='" + status + "', error_message='" + errorMessage + "', " +
                    "count='" + totalFileCount + "', action='', execution_time=" + exec_time + ", " +
                    "modified_on=CURRENT_TIMESTAMP, failure_count='0', count2='" + numberOfRecord + "' WHERE id=" + id;
            logger.info(query);
            stmt.executeUpdate(query);
        } catch (Exception e) {
            logger.error("Unable to update module audit: " + e.getLocalizedMessage());
        }
    }
}
