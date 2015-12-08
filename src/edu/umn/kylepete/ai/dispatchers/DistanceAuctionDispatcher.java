package edu.umn.kylepete.ai.dispatchers;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import edu.umn.kylepete.ai.agents.DistanceBiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Auction;
import edu.umn.kylepete.auctions.AuctionResult;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public class DistanceAuctionDispatcher extends TaxiDispatch {

    private Set<TaxiAgent> taxis;
    
    public DistanceAuctionDispatcher() {
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
        processRequests();
    }

    private void processRequests() {
        PriorityQueue<Bid> allBids = new PriorityQueue<Bid>();
        Auction auction = new Auction(taxis, new DistanceBiddingStrategy());
        for (Request request : requestQueue) {
            AuctionResult results = auction.offer(request);
            allBids.addAll(results.bids);
        }

        Set<Request> assignedRequests = new HashSet<Request>();
        Set<TaxiAgent> assignedTaxis = new HashSet<TaxiAgent>();
        for (Bid bid : allBids) {
            if (!assignedRequests.contains(bid.object) && !assignedTaxis.contains(bid.bidder)) {
                bid.bidder.assignRequest(bid.object);
                requestQueue.remove(bid.object);
                assignedRequests.add(bid.object);
                assignedTaxis.add(bid.bidder);
            }
        }
    }
}
