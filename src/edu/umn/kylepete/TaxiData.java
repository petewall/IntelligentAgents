package edu.umn.kylepete;

import java.util.ArrayList;

public class TaxiData {
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
