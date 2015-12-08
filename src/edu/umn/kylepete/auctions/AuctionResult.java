package edu.umn.kylepete.auctions;

import java.util.PriorityQueue;

import edu.umn.kylepete.ai.agents.TaxiAgent;

public class AuctionResult {
    public PriorityQueue<Bid> bids;

    public AuctionResult(PriorityQueue<Bid> bids) {
        this.bids = bids;
    }
    
    public boolean hasWinner() {
        return bids.size() > 0;
    }

    public Bid getWinningBid() {
        if (hasWinner()) {
            return bids.peek();
        }
        return null;
    }

    public TaxiAgent getWinner() {
        if (hasWinner()) {
            return getWinningBid().bidder;
        }
        return null;
    }
}
