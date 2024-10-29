package com.gl.P5Process;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Process {
    public static void p5(Connection conn) {
        updateTrcStatusInNwl(conn);
    }

    private static void updateTrcStatusInNwl(Connection conn) {
        LocalDate yesterday = LocalDate.now().minusDays(1L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = yesterday.format(formatter);
        String t = "UPDATE app.national_whitelist nw SET nw.trc_imei_status = 1,   nw.trc_modified_time = CURRENT_TIMESTAMP WHERE nw.trc_imei_status IN (0, 3)  AND EXISTS (  SELECT 1  FROM app.mobile_device_repository mdr   WHERE mdr.device_id = nw.tac AND mdr.is_type_approved = '1'  AND mdr.trc_approval_date like '" + formattedDate + "%'   ) ";
        QueryExecuter.runQuery(conn, t);
    }

    private static void updateTrcDateTime(Connection conn) {
        String a = "update sys_param set value =CURRENT_TIMESTAMP where tag ='trc_nwl_typeapprove_last_run_time' ";
        QueryExecuter.runQuery(conn, a);
    }
}

