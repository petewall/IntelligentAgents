package edu.umn.kylepete.ai.agents;

import java.util.PriorityQueue;
import java.util.Queue;

import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public abstract class BiddingStrategy {
    
    public abstract Bid getBidFrom(TaxiAgent bidder, Request request);
    
    public Queue<Bid> getBidsFrom(TaxiAgent bidder, Request... requests) {
        Queue<Bid> bids = new PriorityQueue<Bid>();
        for (Request request : requests) {
            bids.add(getBidFrom(bidder, request));
        }
        return bids;
    }

}
