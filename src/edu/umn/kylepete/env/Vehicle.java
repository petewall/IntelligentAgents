package edu.umn.kylepete.env;

import java.util.Date;

public class Vehicle implements TimeListener {
    private String name;
    private int capacity;
    private Status status;
    private Coordinate currentLocation;
    private Route currentRoute;
    private Coordinate currentDestination;
    private VehicleListener currentListener;

    public enum Status {
        PARKED,     // Parked waiting for the next direction
        DRIVING     // Driving to a destination
    }

    public Vehicle(String name, String type, int capacity, Coordinate startingLocation) {
        this.name = name;
        this.capacity = capacity;
        this.currentLocation = startingLocation;
        this.status = Status.PARKED;
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }
    
    public void driveToLoc(Coordinate loc, VehicleListener callback){
    	this.status = Status.DRIVING;
    	currentListener = callback;
    	currentRoute = OSRM.viaRoute(currentLocation, loc);
    	currentDestination = loc;
    	Date expectedTime = new Date(EnvironmentTime.getCurTime().getTime() + currentRoute.time * 1000);
    	EnvironmentTime.waitForTime(expectedTime, this);
    }

	@Override
	public void ariveAtTime() {
		this.status = Status.PARKED;
		VehicleListener callback = currentListener;
		currentListener = null;
		currentLocation = currentDestination;
		currentRoute = null;
		currentDestination = null;
		callback.arriveAtLoc(this, currentLocation);
	}

	public void cancelCurrentRoute(){
		this.status = Status.PARKED;
		currentLocation = getLocation();
		currentRoute = null;
		currentDestination = null;
		currentListener = null; // TODO should we notify the listener of the cancel?
	}
	
    public Status getStatus() {
        return this.status;
    }
    
    public String toString() {
        return this.getName() + " (" + status + ")";
    }

    public Coordinate getLocation() {
    	if(this.status == Status.DRIVING){
    		// TODO estimate current location between currentLocation and currentDestination
    		return this.currentLocation;
    	}else{
    		return this.currentLocation;
    	}
    }

}
