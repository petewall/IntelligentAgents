package edu.umn.kylepete.ai.dispatchers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umn.kylepete.ai.agents.DistanceWithReassignmentBiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Auction;
import edu.umn.kylepete.auctions.AuctionResult;
import edu.umn.kylepete.env.Request;

public class DistanceAuctionWithReassignmentDispatcher extends TaxiDispatch {

    private Set<TaxiAgent> taxis;
    
    public DistanceAuctionWithReassignmentDispatcher() {
        taxis = new HashSet<TaxiAgent>();
    }
    
    @Override
    public void addTaxi(TaxiAgent taxi) {
        taxis.add(taxi);
    }

    @Override
    public void newRequest(Request event) {
        requestQueue.add(event);
        processRequests();
    }

    @Override
    public void requestComplete(TaxiAgent taxiAgent, Request completedRequest) {
        requestQueue.remove(completedRequest);
        processRequests();
    }

    private void processRequests() {
        Map<TaxiAgent, Request> assignments = new HashMap<TaxiAgent, Request>();

        Auction auction = new Auction(taxis, new DistanceWithReassignmentBiddingStrategy(assignments));
        for (Request request : requestQueue) {
            AuctionResult results = auction.offer(request);
            if (results.hasWinner()) {
                TaxiAgent winner = results.getWinner();
                assignments.put(winner, request);
            }
        }

        for (TaxiAgent winner : assignments.keySet()) {
            Request assignment = assignments.get(winner); 
            if (!assignment.equals(winner.getCurrentRequest())) {
                if (winner.getCurrentRequest() != null) {
                    System.out.println("Stop here.  Reassigning!");
                }
                winner.setRequest(assignment);
            }
        }
    }
}
