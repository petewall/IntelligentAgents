package edu.umn.kylepete.ai.strategies;

import java.util.Arrays;
import java.util.Collection;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.ai.dispatchers.DistanceAuctionWithReassignmentDispatcher;
import edu.umn.kylepete.env.RequestListener;
import edu.umn.kylepete.env.vehicles.Vehicle;

public class DistanceAuctionWithReassignmentStrategy implements AIStrategy {

    DistanceAuctionWithReassignmentDispatcher dispatch = new DistanceAuctionWithReassignmentDispatcher();

    public void addVehicles(Collection<Vehicle> vehicles) {
        for (Vehicle vehicle : vehicles) {
            dispatch.addTaxi(new TaxiAgent(vehicle, dispatch));
        }
    }

    public Collection<? extends RequestListener> getRequestListeners() {
        return Arrays.asList(dispatch);
    }
}
