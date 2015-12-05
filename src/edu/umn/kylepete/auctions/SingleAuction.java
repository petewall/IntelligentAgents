package edu.umn.kylepete.auctions;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class SingleAuction extends Auction {
    public SingleAuction(Set<TaxiAgent> bidders) {
        super(bidders);
    }
    
    @Override
    public TaxiAgent offerOne(Request request) {
        Queue<Bid> bids = new PriorityQueue<Bid>();
        for (TaxiAgent bidder : bidders) {
            Bid bid = bidder.getBid(request);
            if (!bid.abstain) {                
                bids.add(bid);
            }
        }
        
        if (bids.size() > 0) {
            Bid winningBid = bids.remove();
            return winningBid.bidder;
        }
        return null;
    }

    @Override
    public Map<Request, TaxiAgent> offerAll(Request... requests) {
        Map<Request, TaxiAgent> results = new HashMap<Request, TaxiAgent>();
        for (Request request : requests) {
            results.put(request, offerOne(request));
        }
        return results;
    }
}
