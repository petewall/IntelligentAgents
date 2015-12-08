package edu.umn.kylepete.auctions;

import java.util.PriorityQueue;

import edu.umn.kylepete.ai.agents.TaxiAgent;

public class AuctionResult {
    private PriorityQueue<Bid> results;

    public AuctionResult(PriorityQueue<Bid> results) {
        this.results = results;
    }
    
    public boolean hasWinner() {
        return results.size() > 0;
    }

    public Bid getWinningBid() {
        if (hasWinner()) {
            return results.peek();
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
