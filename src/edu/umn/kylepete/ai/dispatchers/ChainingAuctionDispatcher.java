package edu.umn.kylepete.ai.dispatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umn.kylepete.ai.agents.ChainingBiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Auction;
import edu.umn.kylepete.auctions.AuctionResult;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public class ChainingAuctionDispatcher extends TaxiDispatch {

    private Set<TaxiAgent> taxis;
    
    public ChainingAuctionDispatcher() {
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
        Set<Request> requests = new HashSet<Request>();
        requests.addAll(requestQueue);
        ChainingBiddingStrategy biddingStrategy = new ChainingBiddingStrategy(); 
        Auction auction = new Auction(taxis, biddingStrategy);

        while (requests.size() > 0) {
            List<Bid> allBids = new ArrayList<Bid>();
            for (Request request : requests) {
                AuctionResult results = auction.offer(request);
                allBids.addAll(results.bids);
            }
    
            if (allBids.size() == 0) {
                break;
            }
            Collections.sort(allBids);
            Set<Request> assignedRequests = new HashSet<Request>();
            Set<TaxiAgent> assignedTaxis = new HashSet<TaxiAgent>();
            for (Bid bid : allBids) {
                if (!assignedRequests.contains(bid.object) && !assignedTaxis.contains(bid.bidder)) {
                    biddingStrategy.setAssignment(bid);
                    requests.remove(bid.object);
                }
            }
        }
        
        Map<TaxiAgent, List<Request>> assignments = biddingStrategy.getAssignments();
        for (TaxiAgent agent : assignments.keySet()) {
            requestQueue.removeAll(assignments.get(agent));
            agent.assignRequests(assignments.get(agent));
        }
    }
}
