package edu.umn.kylepete.ai.agents;

import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

public abstract class BiddingStrategy {
    
    protected TaxiAgent agent;
    
    public BiddingStrategy(TaxiAgent agent) {
        this.agent = agent;
    }

    public abstract Bid getBid(Request request);

}
