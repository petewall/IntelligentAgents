package edu.umn.kylepete.ai.dispatchers;

import java.util.HashSet;
import java.util.Set;

import edu.umn.kylepete.ai.agents.DistanceBiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Auction;
import edu.umn.kylepete.auctions.AuctionResult;
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
        Auction auction = new Auction(taxis, new DistanceBiddingStrategy());
        Request[] requests = requestQueue.toArray(new Request[requestQueue.size()]);
        for (Request request : requests) {
            AuctionResult results = auction.offer(request);
            if (results.hasWinner()) {
                results.getWinner().assignRequest(request);
                requestQueue.remove(request);
            }
        }
    }
}
