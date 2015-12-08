package edu.umn.kylepete.auctions;

import java.util.Collections;
import java.util.List;

import edu.umn.kylepete.ai.agents.TaxiAgent;

public class AuctionResult {
    public List<Bid> bids;

    public AuctionResult(List<Bid> bids) {
        this.bids = bids;
        Collections.sort(bids);
    }
    
    public boolean hasWinner() {
        return bids.size() > 0;
    }

    public Bid getWinningBid() {
        if (hasWinner()) {
            return bids.get(0);
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
