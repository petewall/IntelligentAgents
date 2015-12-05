package edu.umn.kylepete.ai.agents;

import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public class DistanceBiddingStrategy extends BiddingStrategy {

    public DistanceBiddingStrategy(TaxiAgent agent) {
        super(agent);
    }

    @Override
    public Bid getBid(Request request) {
        Bid bid = new Bid(agent);
        if (request.getNumberOfPassengers() > agent.getVehicle().getCapacity()) {
            bid.abstain = true;
        } else if (agent.getStatus() != TaxiAgent.Status.WAITING) {
            bid.abstain = true;
        } else {
            bid.value = agent.getVehicle().getLocation().distance(request.getPickupLocation());
        }
        return bid;
    }
}
