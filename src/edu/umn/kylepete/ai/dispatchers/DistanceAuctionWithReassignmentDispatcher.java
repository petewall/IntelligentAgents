package edu.umn.kylepete.ai.dispatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umn.kylepete.ai.agents.DistanceWithReassignmentBiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Auction;
import edu.umn.kylepete.auctions.AuctionResult;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public class DistanceAuctionWithReassignmentDispatcher extends TaxiDispatch {

    private Set<TaxiAgent> taxis;
    private Map<Request, TaxiAgent> assignments;
    
    public DistanceAuctionWithReassignmentDispatcher() {
        taxis = new HashSet<TaxiAgent>();
        assignments = new HashMap<Request, TaxiAgent>();
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
        assignments.remove(completedRequest);
        processRequests();
    }

    private void processRequests() {
        List<Bid> allBids = new ArrayList<Bid>();
        Auction auction = new Auction(taxis, new DistanceWithReassignmentBiddingStrategy());
        for (Request request : requestQueue) {
            AuctionResult results = auction.offer(request);
            allBids.addAll(results.bids);
        }

        Collections.sort(allBids);
        Set<Request> assignedRequests = new HashSet<Request>();
        Set<TaxiAgent> assignedTaxis = new HashSet<TaxiAgent>();
        for (Bid bid : allBids) {
            if (!assignedRequests.contains(bid.object) && !assignedTaxis.contains(bid.bidder)) {
                if (assignments.containsKey(bid.object)) {
                    if (assignments.get(bid.object).equals(bid.bidder)) {
                        assignedRequests.add(bid.object);
                        assignedTaxis.add(bid.bidder);
                        continue;
                    }
                    System.out.println("Reassigning - I took someone elses");
                    assignments.get(bid.object).clearRequests();
                }
                assignments.put(bid.object, bid.bidder);
                if (bid.bidder.getCurrentRequest() != null) {
                    assignments.remove(bid.bidder.getCurrentRequest());
                    System.out.println("Reassigning - I'm getting rid of my own");
                    bid.bidder.clearRequests();
                }
                bid.bidder.assignRequest(bid.object);
                assignedRequests.add(bid.object);
                assignedTaxis.add(bid.bidder);
            }
        }
    }
}
