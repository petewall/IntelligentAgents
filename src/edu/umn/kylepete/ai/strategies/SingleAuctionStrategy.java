package edu.umn.kylepete.ai.strategies;

import java.util.Arrays;
import java.util.Collection;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.ai.dispatchers.SingleAuctionDispatcher;
import edu.umn.kylepete.env.RequestListener;
import edu.umn.kylepete.env.vehicles.Vehicle;

public class SingleAuctionStrategy implements AIStrategy {

	SingleAuctionDispatcher dispatch = new SingleAuctionDispatcher();

	@Override
	public void addVehicles(Collection<Vehicle> vehicles) {
		for (Vehicle vehicle : vehicles) {
			dispatch.addTaxi(new TaxiAgent(vehicle, dispatch));
		}
	}

	@Override
	public Collection<? extends RequestListener> getRequestListeners() {
		return Arrays.asList(dispatch);
	}

}
