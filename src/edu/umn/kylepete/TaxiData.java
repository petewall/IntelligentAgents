package edu.umn.kylepete;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

public class TaxiData {
    private Connection conn;
    
    public TaxiData() {
        try {
            String url = "jdbc:postgresql://192.168.0.100/taxidata";
            Properties props = new Properties();
            props.setProperty("user","taxiuser");
            props.setProperty("password","zI3WcHKFAuHx71ny7efHjifri9JmPh");
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
    public long getStartTime() {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT MIN(pickup_datetime) FROM trip");
            ResultSet result = statement.executeQuery();
            Timestamp date = result.getTimestamp(0);
            statement.close();
            return date.getTime();
        } catch (SQLException e) {
            Logger.error("DB", "SQL execption: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
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
