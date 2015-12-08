package edu.umn.kylepete.ai.agents;

import java.util.LinkedList;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.ai.dispatchers.TaxiDispatch;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.env.vehicles.Vehicle.VehicleNotAtRouteStartException;
import edu.umn.kylepete.env.vehicles.VehicleListener;
import edu.umn.kylepete.stats.RequestStats;

public class TaxiAgent implements VehicleListener {
	
	private Vehicle vehicle;
	private TaxiDispatch dispatch;
	private LinkedList<Request> requests;
	private Status status;

	public enum Status {
		WAITING, // Waiting for a request
		PICKING_UP, // Driving to pick up a request
		DRIVING // Driving a request to the destination
	}

	public TaxiAgent(Vehicle vehicle, TaxiDispatch dispatch){
		this.vehicle = vehicle;
		this.requests = new LinkedList<Request>();
		this.status = Status.WAITING;
		this.dispatch = dispatch;
	}
	
	public Status getStatus(){
		return this.status;
	}
	
	public Vehicle getVehicle() {
	    return vehicle;
	}
	
	public Request getCurrentRequest() {
	    return requests.getFirst();
	}
	
	public String toString() {
	    return vehicle.toString();
	}
	
	private void startNextRequest() {
	    if (requests.size() > 0) {
	        status = Status.PICKING_UP;
	        Logger.debug("TAXI AGENT", vehicle.toString() + " --> " + status);
	        vehicle.driveToLoc(getCurrentRequest().getPickupLocation(), this);
	    }
	}
	
	private void driveRequestToDestination() {
        status = Status.DRIVING;
        RequestStats.addIdleTime((vehicle.getEnvironmentTime().getCurTime().getTime() - getCurrentRequest().getSubmitTime().getTime()) / 1000);
        Logger.debug("TAXI AGENT", vehicle.toString() + " --> " + status);
        try {
            this.vehicle.driveRoute(getCurrentRequest().getRoute(), this);
        } catch (VehicleNotAtRouteStartException e) {
            Logger.error("TAXI AGENT", "Vehicle's current location does not match the pickup location");
            // This shouldn't happen because we just drove to the route pickup coordinate
            // but if it does, just drive from the vehicle's current location to the dropoff
            // this causes an extra call to OSRM
            this.vehicle.driveToLoc(getCurrentRequest().getDropoffLocation(), this);
        }
	}
	
	private void completeRequest() {
        status = Status.WAITING;
        RequestStats.requestFulfilled();
        Logger.debug("TAXI AGENT", vehicle.toString() + " --> " + status);
        
        if (requests.size() > 1) {
            System.out.println("Stop here");
        }
        Request completedRequest = requests.removeFirst();
        startNextRequest();
        dispatch.requestComplete(this, completedRequest);
	}
	
	public void arriveAtLoc(Vehicle vehicle, Coordinate loc) {
		if (status == Status.PICKING_UP) {
		    driveRequestToDestination();
		} else if (status == Status.DRIVING) {
		    completeRequest();
		}
	}

	public void assignRequest(Request request){
	    requests.addLast(request);
	    if (requests.size() == 1) {
	        startNextRequest();
	    } else {
	        System.out.println("Stop here");
	    }
	}
}
