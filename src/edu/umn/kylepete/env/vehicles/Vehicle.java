package edu.umn.kylepete.env.vehicles;

import java.text.ParseException;
import java.util.Date;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.OSRM;
import edu.umn.kylepete.env.Route;
import edu.umn.kylepete.env.TimeListener;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.stats.VehicleStats;

public abstract class Vehicle implements TimeListener {
    private String name;
    private boolean driving;
    private Date timeSince; // Used to calculate how long this car has been driving or parked
    private Coordinate currentLocation;
    private Route currentRoute;
    private VehicleListener currentListener;

    public Vehicle(String name, Coordinate startingLocation) {
        this.name = name;
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

    public abstract int getCapacity();

    public void driveToLoc(Coordinate loc, VehicleListener callback){
        VehicleStats.addParked(EnvironmentTime.getElapsed(timeSince));
        this.timeSince = EnvironmentTime.getCurTime();
    	this.driving = true;
    	currentListener = callback;
    	currentRoute = OSRM.viaRoute(currentLocation, loc);
		if (currentRoute.time == 0) {
			// we are already at the location
			ariveAtTime();
		} else {
			Date expectedTime = new Date(EnvironmentTime.getCurTime().getTime() + currentRoute.time * 1000);
			try {
				EnvironmentTime.waitForTime(expectedTime, this);
			} catch (EnvironmentTimeException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
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
