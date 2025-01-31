package com.gl.P5Process;

import com.gl.Audit.ModulesAudit;
import java.sql.Connection;
import com.gl.Application.*;

import static com.gl.Application.totalcount;

public class Process {
    public static void p5(Connection conn, int auditId, long startTime) {
        updateTrcStatusInNwl(conn);
        int totalRecordCount = getTotalRecordCount(conn);
        updateTrcDateTime(conn);
        ModulesAudit.updateModuleAudit(conn, 200, "SUCCESS", "", auditId, startTime, totalRecordCount, totalcount);
    }

    private static void updateTrcStatusInNwl(Connection conn) {
        String formattedDate = QueryExecuter.getLastRunDate(conn);
        String query = "UPDATE app.national_whitelist nw SET nw.trc_imei_status = 1, nw.trc_modified_time = CURRENT_TIMESTAMP " +
                       "WHERE nw.trc_imei_status IN (0, 3) AND EXISTS (SELECT 1 FROM app.mobile_device_repository mdr " +
                       "WHERE mdr.device_id = nw.tac AND mdr.is_type_approved = '1' AND mdr.trc_approval_date >= '" + formattedDate + "')";
        totalcount+= QueryExecuter.runQuery(conn, query);
    }

    private static void updateTrcDateTime(Connection conn) {
        String query = "UPDATE sys_param SET value = CURRENT_TIMESTAMP WHERE tag = 'trc_nwl_typeapprove_last_run_time'";
        QueryExecuter.runQuery(conn, query);
    }

    private static int getTotalRecordCount(Connection conn) {
        String query = "SELECT COUNT(*) FROM app.national_whitelist";
        return QueryExecuter.runQuery(conn, query);
    }
}