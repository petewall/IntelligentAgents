package edu.umn.kylepete.ai.dispatchers;

import java.util.HashSet;
import java.util.Set;

import edu.umn.kylepete.ai.agents.DistanceBiddingStrategy;
import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Auction;
import edu.umn.kylepete.env.Request;

/**
 * The SingleAuctionDispatcher runs an auction for a single request.
 * @author pwall
 *
 */
public class SingleAuctionDispatcher extends TaxiDispatch {

    private Set<TaxiAgent> taxis;
    
    public SingleAuctionDispatcher() {
        taxis = new HashSet<TaxiAgent>();
    }
    
    @Override
    public void addTaxi(TaxiAgent taxi) {
        taxis.add(taxi);
    }

    @Override
    public void newRequest(Request event) {
        requestQueue.add(event);
        processRequests();
    }

    @Override
    public void requestComplete(TaxiAgent taxiAgent, Request completedRequest) {
        processRequests();
    }

    private void processRequests() {
        Request[] requests = requestQueue.toArray(new Request[requestQueue.size()]);
        Auction auction = new Auction(taxis, new DistanceBiddingStrategy());

        for (Request request : requests) {
            TaxiAgent winner = auction.offer(request);
            if (winner != null) {
                winner.assignRequest(request);
                requestQueue.remove(request);
            }
        }
    }
}
