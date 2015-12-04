package edu.umn.kylepete.ai;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.ai.dispatchers.TaxiDispatch;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.Vehicle;
import edu.umn.kylepete.env.VehicleListener;
import edu.umn.kylepete.stats.RequestStats;

public class TaxiAgent implements VehicleListener {
	
	private Vehicle vehicle;
	private TaxiDispatch dispatch;
	private Request currentRequest;
	private Status status;

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
	}
	
	public Status getStatus(){
		return this.status;
	}
	
	public void arriveAtLoc(Vehicle vehicle, Coordinate loc) {
		if (status == Status.PICKING_UP) {
			status = Status.DRIVING;
			Logger.debug(vehicle.toString() + " --> " + status);
			this.vehicle.driveToLoc(currentRequest.getDropoffLocation(), this);
            RequestStats.addIdleTime((EnvironmentTime.getCurTime().getTime() - currentRequest.getTime().getTime()) / 1000);
		} else if (status == Status.DRIVING) {
			status = Status.WAITING;
			Logger.debug(vehicle.toString() + " --> " + status);
			currentRequest = null;
			dispatch.requestComplete(this);
            RequestStats.requestFulfilled();
		}
	}

	public void fulfillRequest(Request request){
		currentRequest = request;
		status = Status.PICKING_UP;
		Logger.debug(vehicle.toString() + " --> " + status);
		vehicle.driveToLoc(request.getPickupLocation(), this);
	}
}
