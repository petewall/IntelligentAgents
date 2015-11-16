package edu.umn.kylepete;

public class Request {
    private Coordinate pickup;
    private Coordinate dropoff;
    private int passengers;
    
    public Request(Coordinate pickup, Coordinate dropoff, int passengers) {
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.passengers = passengers;
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
