package edu.umn.kylepete.ai.agents;

import java.util.Map;

import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public class DistanceWithReassignmentBiddingStrategy extends BiddingStrategy {
    
    private Map<TaxiAgent, Request> assignments;
    
    public DistanceWithReassignmentBiddingStrategy(Map<TaxiAgent, Request> assignments) {
        this.assignments = assignments;
    }

    @Override
    public Bid getBidFrom(TaxiAgent agent, Request request) {
        Bid bid = new Bid(agent, request);
        if (assignments.containsKey(agent)) {
            bid.abstain = true;
        } else if (request.getNumberOfPassengers() > agent.getVehicle().getCapacity()) {
            bid.abstain = true;
        } else if (agent.getStatus() == TaxiAgent.Status.DRIVING) {
            bid.abstain = true;
        } else {
            bid.value = agent.getVehicle().getLocation().distance(request.getPickupLocation());
        }
        return bid;
    }
}
