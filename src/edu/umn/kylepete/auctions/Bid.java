package edu.umn.kylepete.auctions;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class Bid implements Comparable<Bid> {
    public TaxiAgent bidder;
    public Request object;
    public boolean abstain;
    public double value;
    
    public Bid(TaxiAgent bidder, Request object) {
        this.bidder = bidder;
        this.object = object;
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
