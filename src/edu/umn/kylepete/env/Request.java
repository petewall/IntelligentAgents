package edu.umn.kylepete.env;

import java.util.Date;

public class Request {
    private Date time;
    private Coordinate pickup;
    private Coordinate dropoff;
    private double distance;
    private int passengers;
    
    public Request(Date time, Coordinate pickup, Coordinate dropoff, int passengers) {
        this.time = time;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.distance = pickup.distance(dropoff); // FIXME Should this be made truly accurate using OSRM? 
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
    
    public double getDistance() {
        return distance;
    }
    
    public int getNumberOfPassengers() {
        return passengers;
    }
    
    public String toString() {
        return "Request: " + passengers + " passenger(s) from " + pickup + " to " + dropoff;
    }
}
