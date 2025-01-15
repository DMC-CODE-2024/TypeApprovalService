/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gl;

import com.gl.Config.MySQLConnection;
import com.gl.Config.PropertyReader;
import com.gl.P5Process.Process;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Application {
    public static PropertyReader propertyReader = new PropertyReader();
    static Logger log = LogManager.getLogger(Application.class);
    public static String serverName;

    public static void main(String[] args) {
        try {
            serverName = propertyReader.getConfigPropValue("serverName");
        } catch (Exception e) {
            log.error("Not able to fetch information for serverName " + e);
        }
       try (var c = new MySQLConnection().getConnection()) {
            Process.p5(c);
        } catch (Exception e) {
            log.error("Error in Service  " + e);
        }
       // .p5(new MySQLConnection().getConnection());
        System.exit(0);
    }
}
