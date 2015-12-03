package edu.umn.kylepete.env;

import java.util.Date;

public class Vehicle implements TimeListener {
    private String name;
    private int capacity;
    private boolean driving;
    private Coordinate currentLocation;
    private Route currentRoute;
    private Coordinate currentDestination;
    private VehicleListener currentListener;

    public Vehicle(String name, String type, int capacity, Coordinate startingLocation) {
        this.name = name;
        this.capacity = capacity;
        this.currentLocation = startingLocation;
        this.driving = false;
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }
    
    public void driveToLoc(Coordinate loc, VehicleListener callback){
    	this.driving = true;
    	currentListener = callback;
    	currentRoute = OSRM.viaRoute(currentLocation, loc);
    	currentDestination = loc;
    	Date expectedTime = new Date(EnvironmentTime.getCurTime().getTime() + currentRoute.time * 1000);
    	EnvironmentTime.waitForTime(expectedTime, this);
    }

	public void ariveAtTime() {
		this.driving = false;
		VehicleListener callback = currentListener;
		currentListener = null;
		currentLocation = currentDestination;
		currentRoute = null;
		currentDestination = null;
		callback.arriveAtLoc(this, currentLocation);
	}

	public void cancelCurrentRoute(){
		this.driving = false;
		currentLocation = getLocation();
		currentRoute = null;
		currentDestination = null;
		currentListener = null; // TODO should we notify the listener of the cancel?
	}
	
    public boolean isDriving() {
        return this.driving;
    }
    
    public String toString() {
    	String drivingStr = "PARKED";
    	if(driving){
    		drivingStr = "DRIVING";
    	}
        return this.getName() + " (" + drivingStr + ")";
    }

    public Coordinate getLocation() {
    	if(this.driving){
    		// TODO estimate current location between currentLocation and currentDestination
    		return this.currentLocation;
    	}else{
    		return this.currentLocation;
    	}
    }

}
