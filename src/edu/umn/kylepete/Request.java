package edu.umn.kylepete;

import java.util.Date;

public class Request {
    private Date time;
    private Coordinate pickup;
    private Coordinate dropoff;
    private int passengers;
    
    public Request(Date time, Coordinate pickup, Coordinate dropoff, int passengers) {
        this.time = time;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.passengers = passengers;
    }
    
    public Date getTime() {
        return time;
    }

    public Coordinate getPickupLocation() {
        return pickup;
    }
    
    public Coordinate getDropoffLocation() {
        return dropoff;
    }
    
    public int getNumberOfPassengers() {
        return passengers;
    }
}
