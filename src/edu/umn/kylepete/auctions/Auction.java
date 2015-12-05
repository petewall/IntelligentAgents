package edu.umn.kylepete.auctions;

import java.util.Map;
import java.util.Set;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;

public abstract class Auction {
    protected Set<TaxiAgent> bidders;
    
    public Auction(Set<TaxiAgent> bidders) {
        this.bidders = bidders;
    }

    public abstract TaxiAgent offerOne(Request request);

    public abstract Map<Request, TaxiAgent> offerAll(Request... requests);
}
