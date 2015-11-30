package edu.umn.kylepete.db;

import java.util.List;

import edu.umn.kylepete.env.Request;

public abstract class TaxiData {

    public TaxiData() {
        super();
    }

    /**
     * Get the earliest time in the DB
     * @return
     */
    public abstract long getStartTime();

    /**
     * Get all of the requests between a given time period
     * @param startTime
     * @param endTime
     * @return
     */
    public abstract List<Request> getRequests(long startTime, long endTime);
    
    public abstract Request getNextRequest();

}