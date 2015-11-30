package edu.umn.kylepete.ai;

import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.Vehicle;
import edu.umn.kylepete.env.VehicleListener;

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
	}
	
	public Status getStatus(){
		return this.status;
	}
	
	@Override
	public void arriveAtLoc(Vehicle vehicle, Coordinate loc) {
		if(status == Status.PICKING_UP){
			status = Status.DRIVING;
			this.vehicle.driveToLoc(currentRequest.getDropoffLocation(), this);
		}else if(status == Status.DRIVING){
			status = Status.WAITING;
			currentRequest = null;
			dispatch.requestComplete(this);
		}
	}
	
	public void fulfillRequest(Request request){
		currentRequest = request;
		status = Status.PICKING_UP;
		vehicle.driveToLoc(request.getPickupLocation(), this);
	}
}
