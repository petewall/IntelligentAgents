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
    
    public String toString() {
        if (abstain) {
            return bidder.toString() + " abstains";
        } else {
            return bidder.toString() + " bids " + Double.toString(value);
        }
    }
    
    public int compareTo(Bid otherBid) {
        return Double.compare(value, otherBid.value);
    }
}
