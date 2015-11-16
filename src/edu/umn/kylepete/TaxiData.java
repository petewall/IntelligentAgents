package edu.umn.kylepete;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class TaxiData {
    private Connection conn;
    
    public TaxiData() {
        try {
            String url = "jdbc:postgresql://localhost/test";
            Properties props = new Properties();
            props.setProperty("user","taxiuser");
            props.setProperty("password","zI3WcHKFAuHx71ny7efHjifri9JmPh");
            props.setProperty("ssl","true");
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            Logger.error("DATA", "Failed to open connection: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
    }

    /**
     * Get the earliest time in the DB
     * @return
     */
    public int getStartTime() {
        return 0;
    }
    
    /**
     * Get all of the requests between a given time period
     * @param startTime
     * @param endTime
     * @return
     */
    public ArrayList<Request> getRequests(int startTime, int endTime) {
        return new ArrayList<Request>();
    }
    

}
