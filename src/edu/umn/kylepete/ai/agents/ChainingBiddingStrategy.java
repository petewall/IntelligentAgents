package edu.umn.kylepete.ai.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.Request;

public class ChainingBiddingStrategy extends BiddingStrategy {
    
    private int chainingLimit = 3;
    private Map<TaxiAgent, List<Request>> assignments;
    
    public ChainingBiddingStrategy() {
        assignments = new HashMap<TaxiAgent, List<Request>>();
    }
    
    public void setAssignment(Bid bid) {
        if (!assignments.containsKey(bid.bidder)) {
            assignments.put(bid.bidder, new ArrayList<Request>());
        }
        assignments.get(bid.bidder).add(bid.object);
    }
    
    public Map<TaxiAgent, List<Request>> getAssignments() {
        return assignments;
    }
    
    public double getFullPathLength(TaxiAgent agent) {
        double distance = 0;
        Coordinate location = agent.getVehicle().getLocation();
        for (Request request : assignments.get(agent)) {
            distance += location.distance(request.getPickupLocation());
            distance += request.getDistance();
            location = request.getDropoffLocation();
        }
        return distance;
    }

    @Override
    public Bid getBidFrom(TaxiAgent agent, Request request) {
        Bid bid = new Bid(agent, request);
        if (request.getNumberOfPassengers() > agent.getVehicle().getCapacity()) {
            bid.abstain = true;
        } else if (agent.getStatus() != TaxiAgent.Status.WAITING) {
            bid.abstain = true;
        } else {
            if (assignments.containsKey(agent)) {
                List<Request> currentAssignments = assignments.get(agent);
                if (currentAssignments.size() == 0) { 
                    bid.value = agent.getVehicle().getLocation().distance(request.getPickupLocation());
                } else if (currentAssignments.size() >= chainingLimit) {
                    bid.abstain = true;
                } else {
                    bid.value = getFullPathLength(agent);
                }
            } else {
                bid.value = agent.getVehicle().getLocation().distance(request.getPickupLocation());
            }
        }
        return bid;
    }
}
