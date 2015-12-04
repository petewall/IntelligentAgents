package edu.umn.kylepete.env;

import java.text.ParseException;
import java.util.Date;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.stats.VehicleStats;

public class Vehicle implements TimeListener {
    private String name;
    private int capacity;
    private boolean driving;
    private Date timeSince; // Used to calculate how long this car has been driving or parked
    private Coordinate currentLocation;
    private Route currentRoute;
    private VehicleListener currentListener;

    public Vehicle(String name, String type, int capacity, Coordinate startingLocation) {
        this.name = name;
        this.capacity = capacity;
        this.currentLocation = startingLocation;
        this.driving = false;
        try {
            this.timeSince = TaxiSystemProperties.getTimeStart();
        } catch (ParseException e) {
            Logger.error("VEHICLE", "Failed to get the start time");
            Logger.error("VEHICLE", Logger.stackTraceToString(e));
        }
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void driveToLoc(Coordinate loc, VehicleListener callback){
        VehicleStats.addParked(EnvironmentTime.getElapsed(timeSince));
        this.timeSince = EnvironmentTime.getCurTime();
    	this.driving = true;
    	currentListener = callback;
    	currentRoute = OSRM.viaRoute(currentLocation, loc);
    	Date expectedTime = new Date(EnvironmentTime.getCurTime().getTime() + currentRoute.time * 1000);
    	EnvironmentTime.waitForTime(expectedTime, this);
    }

	public void ariveAtTime() {
        VehicleStats.addDrive(currentRoute.distance, EnvironmentTime.getElapsed(timeSince));
        this.timeSince = EnvironmentTime.getCurTime();
		this.driving = false;
		VehicleListener callback = currentListener;
		currentListener = null;
		currentLocation = currentRoute.points[currentRoute.points.length - 1];
		currentRoute = null;
		callback.arriveAtLoc(this, currentLocation);
	}

	public void cancelCurrentRoute(){
		this.driving = false;
		currentLocation = getLocation();
		currentRoute = null;
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
