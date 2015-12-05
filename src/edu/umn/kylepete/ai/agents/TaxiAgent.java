package edu.umn.kylepete.ai.agents;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.ai.dispatchers.TaxiDispatch;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.env.vehicles.VehicleListener;
import edu.umn.kylepete.stats.RequestStats;

public class TaxiAgent implements VehicleListener {
	
	private Vehicle vehicle;
	private TaxiDispatch dispatch;
	private Request currentRequest;
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
	
	public String toString() {
	    return vehicle.toString();
	}
	
	public void arriveAtLoc(Vehicle vehicle, Coordinate loc) {
		if (status == Status.PICKING_UP) {
			status = Status.DRIVING;
			RequestStats.addIdleTime((EnvironmentTime.getCurTime().getTime() - currentRequest.getTime().getTime()) / 1000);
			Logger.debug(vehicle.toString() + " --> " + status);
			this.vehicle.driveToLoc(currentRequest.getDropoffLocation(), this);
		} else if (status == Status.DRIVING) {
			status = Status.WAITING;
			RequestStats.requestFulfilled();
			Logger.debug(vehicle.toString() + " --> " + status);
			currentRequest = null;
			dispatch.requestComplete(this);
		}
	}

	public void fulfillRequest(Request request){
		currentRequest = request;
		status = Status.PICKING_UP;
		Logger.debug(vehicle.toString() + " --> " + status);
		vehicle.driveToLoc(request.getPickupLocation(), this);
	}
	
	public Bid getBid(Request request) {
	    return this.biddingStrategy.getBid(request);
	}
}
