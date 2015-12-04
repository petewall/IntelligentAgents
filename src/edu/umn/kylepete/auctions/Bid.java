package edu.umn.kylepete.auctions;

import edu.umn.kylepete.ai.agents.TaxiAgent;

public class Bid implements Comparable<Bid> {
    public TaxiAgent bidder;
    public boolean abstain;
    public double value;
    
    public Bid(TaxiAgent bidder) {
        this.bidder = bidder;
        this.abstain = false;
        this.value = 0;
    }
    
    public int compareTo(Bid otherBid) {
        return Double.compare(value, otherBid.value);
    }
}
