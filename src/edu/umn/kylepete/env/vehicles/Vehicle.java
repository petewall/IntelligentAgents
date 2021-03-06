package edu.umn.kylepete.env.vehicles;

import java.util.Date;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.env.OSRM;
import edu.umn.kylepete.env.Route;
import edu.umn.kylepete.env.TimeListener;
import edu.umn.kylepete.stats.VehicleStats;

public abstract class Vehicle implements TimeListener {
    private String name;
    private boolean driving;
    private Date timeSince; // Used to calculate how long this car has been driving or parked
    private Date expectedArrival;
    private Coordinate currentLocation;
    private Route currentRoute;
    private VehicleListener currentListener;
	private EnvironmentTime environmentTime;
	
	@SuppressWarnings("serial")
	public static class VehicleNotAtRouteStartException extends Exception {};

	public Vehicle(String name, Coordinate startingLocation, EnvironmentTime time) {
        this.name = name;
        this.currentLocation = startingLocation;
        this.driving = false;
		this.environmentTime = time;
		this.timeSince = environmentTime.getCurTime();
    }

	public EnvironmentTime getEnvironmentTime() {
		return this.environmentTime;
	}

    public String getName() {
        return this.name;
    }

    public abstract int getCapacity();
    
    public void reportTimeParked() {
		VehicleStats.addParked(environmentTime.getElapsed(timeSince));
    }
    
	public void driveRoute(Route route, VehicleListener callback) throws VehicleNotAtRouteStartException {
	    if (currentRoute != null) {
	        Logger.error(getName(), "Driving to new route before the existing one was cancelled!");
	    }
    	if(!currentLocation.equals(route.getStartCoordinate())){
			throw new VehicleNotAtRouteStartException();
    	}
        reportTimeParked();
		this.timeSince = environmentTime.getCurTime();
    	this.driving = true;
    	currentListener = callback;
    	currentRoute = route;
		if (currentRoute.getTime() == 0) {
			// we are already at the location
			ariveAtTime();
		} else {
		    expectedArrival = new Date(environmentTime.getCurTime().getTime() + currentRoute.getTime() * 1000);
			try {
				environmentTime.waitForTime(expectedArrival, this);
			} catch (EnvironmentTimeException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
    }

    public void driveToLoc(Coordinate loc, VehicleListener callback){
		Route route = OSRM.viaRoute(currentLocation, loc);
		// trust OSRM to snap our current location location to the nearest valid location
		currentLocation = route.getStartCoordinate();
		try {
			driveRoute(route, callback);
		} catch (VehicleNotAtRouteStartException e) {
			// this shouldn't happen because we just set our current location to the route start location
			throw new IllegalStateException("Unexpected vehicle not at route start", e);
		}
    }

	public void ariveAtTime() {
		VehicleStats.addDrive(currentRoute.getDistance(), environmentTime.getElapsed(timeSince));
		this.timeSince = environmentTime.getCurTime();
		this.driving = false;
		VehicleListener callback = currentListener;
		currentListener = null;
		currentLocation = currentRoute.getEndCoordinate();
		currentRoute = null;
		expectedArrival = null;
		callback.arriveAtLoc(this, currentLocation);
	}

	public void cancelCurrentRoute() {
	    if (driving) {
            environmentTime.cancelTime(expectedArrival, this);
	        currentLocation = getLocation();
            driving = false;
	        currentRoute = null;
	        currentListener = null; // TODO should we notify the listener of the cancel?
	        expectedArrival = null;
	    }
	}

    public boolean isDriving() {
        return this.driving;
    }

    public String toString() {
        return this.getName() + " (" + (driving ? "DRIVING" : "PARKED") + ")";
    }

    public Coordinate getLocation() {
    	if (this.driving) {
    	    long elapsed = environmentTime.getElapsed(timeSince);
    	    if (elapsed > 0) {
    	        Coordinate a = currentRoute.getStartCoordinate();
    	        Coordinate b = currentRoute.getEndCoordinate();
    	        double percent = 1.0 * elapsed / currentRoute.getTime();
    	        double newLatitude = a.latitude + percent * (a.latitude - b.latitude);
    	        double newLongitude = a.longitude + percent * (a.longitude - b.longitude); 
    	        return new Coordinate(newLatitude, newLongitude);
    	    }
        }
		return this.currentLocation;
    }

}
