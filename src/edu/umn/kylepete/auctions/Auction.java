package edu.umn.kylepete.auctions;

import java.util.PriorityQueue;
import java.util.Set;

import edu.umn.kylepete.ai.agents.BiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class Auction {
    protected Set<TaxiAgent> bidders;
    protected BiddingStrategy biddingStrategy;
    
    public Auction(Set<TaxiAgent> bidders, BiddingStrategy biddingStrategy) {
        this.bidders = bidders;
        this.biddingStrategy = biddingStrategy;
    }

    public AuctionResult offer(Request request) {
        PriorityQueue<Bid> bids = new PriorityQueue<Bid>();
        for (TaxiAgent bidder : bidders) {
            Bid bid = biddingStrategy.getBidFrom(bidder, request);
            if (!bid.abstain) {                
                bids.add(bid);
            }
        }

        return new AuctionResult(bids);
    }
}
