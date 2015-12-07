package edu.umn.kylepete.ai.agents;

import java.util.LinkedList;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.ai.dispatchers.TaxiDispatch;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.env.vehicles.VehicleListener;
import edu.umn.kylepete.stats.RequestStats;

public class TaxiAgent implements VehicleListener {
	
	private Vehicle vehicle;
	private TaxiDispatch dispatch;
	private Request currentRequest;
	private LinkedList<Request> pendingRequests;
	private Status status;
	private BiddingStrategy biddingStrategy;

	public enum Status {
		WAITING, // Waiting for a request
		PICKING_UP, // Driving to pick up a request
		DRIVING // Driving a request to the destination
	}

	public TaxiAgent(Vehicle vehicle, TaxiDispatch dispatch){
		this.vehicle = vehicle;
		this.currentRequest = null;
		this.pendingRequests = new LinkedList<Request>();
		this.status = Status.WAITING;
		this.dispatch = dispatch;
		this.biddingStrategy = new DistanceBiddingStrategy(this);
	}
	
	public Status getStatus(){
		return this.status;
	}
	
	public Vehicle getVehicle() {
	    return vehicle;
	}
	
	public Request getCurrentRequest() {
	    return currentRequest;
	}
	
	public String toString() {
	    return vehicle.toString();
	}
	
	private void pickupRequest(Request request) {
        currentRequest = request;
        status = Status.PICKING_UP;
		Logger.debug("TAXI AGENT", vehicle.toString() + " --> " + status);
        vehicle.driveToLoc(request.getPickupLocation(), this);
	}
	
	public void arriveAtLoc(Vehicle vehicle, Coordinate loc) {
		if (status == Status.PICKING_UP) {
			status = Status.DRIVING;
			RequestStats.addIdleTime((vehicle.getEnvironmentTime().getCurTime().getTime() - currentRequest.getTime().getTime()) / 1000);
			Logger.debug("TAXI AGENT", vehicle.toString() + " --> " + status);
			this.vehicle.driveToLoc(currentRequest.getDropoffLocation(), this);
		} else if (status == Status.DRIVING) {
			status = Status.WAITING;
			RequestStats.requestFulfilled();
			Logger.debug("TAXI AGENT", vehicle.toString() + " --> " + status);
			
			if (pendingRequests.size() > 0) {
			    pickupRequest(pendingRequests.removeFirst());
			} else {
			    currentRequest = null;
			    dispatch.requestComplete(this);
			}
		}
	}

	public void assignRequest(Request request){
	    if (currentRequest == null) {
	        pickupRequest(request);
	    } else {
	        pendingRequests.addLast(request);
	    }
	}
	
	public double getDistanceUntilComplete() {
	    double distance = 0;
	    if (currentRequest != null) {
	        if (status == Status.PICKING_UP) {
                distance += vehicle.getLocation().distance(currentRequest.getPickupLocation());             
                distance += currentRequest.getDistance();
	        } else if (status == Status.DRIVING) {
	            distance += vehicle.getLocation().distance(currentRequest.getDropoffLocation());
	        }
	        
            Coordinate lastLocation = currentRequest.getDropoffLocation();
	        for (Request pendingRequest : pendingRequests) {
	            distance += lastLocation.distance(pendingRequest.getPickupLocation());
	            distance += pendingRequest.getDistance();
	            lastLocation = pendingRequest.getDropoffLocation();
	        }
	    }
	    return distance;
	}
	
	public Coordinate getFinalLocation() {
	    if (pendingRequests.size() > 0) {
	        return pendingRequests.getLast().getDropoffLocation();
	    }
        if (currentRequest != null) {
            return currentRequest.getDropoffLocation();
        }
        return vehicle.getLocation();
	}
	
	public Bid getBid(Request request) {
	    return this.biddingStrategy.getBid(request);
	}
}
