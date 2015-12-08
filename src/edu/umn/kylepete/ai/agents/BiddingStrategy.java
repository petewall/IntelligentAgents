package edu.umn.kylepete.ai.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public abstract class BiddingStrategy {
    
    public abstract Bid getBidFrom(TaxiAgent bidder, Request request);
    
    public List<Bid> getBidsFrom(TaxiAgent bidder, Request... requests) {
        List<Bid> bids = new ArrayList<Bid>();
        for (Request request : requests) {
            bids.add(getBidFrom(bidder, request));
        }
        Collections.sort(bids);
        return bids;
    }

}
