package edu.umn.kylepete.auctions;

import java.util.Queue;

import edu.umn.kylepete.ai.agents.TaxiAgent;

public class AuctionResult {
    private Queue<Bid> results;
    
    public AuctionResult(Queue<Bid> bids) {
        this.results = bids;
    }
    
    public TaxiAgent getWinner() {
        if (results.size() > 0) {
            return results.peek().bidder;
        }
        return null;
    }

    public boolean hasWinner() {
        return results.size() > 0;
    }
}
