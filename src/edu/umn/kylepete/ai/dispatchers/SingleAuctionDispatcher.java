package edu.umn.kylepete.ai.dispatchers;

import java.util.HashSet;
import java.util.Set;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.SingleAuction;
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
    public void requestComplete(TaxiAgent taxiAgent) {
        processRequests();
    }

    private void processRequests() {
        Request[] requests = requestQueue.toArray(new Request[requestQueue.size()]);
        for (Request request : requests) {
            SingleAuction auction = new SingleAuction(taxis);
            TaxiAgent winner = auction.offerOne(request);
            if (winner != null) {
                winner.assignRequest(request);
                requestQueue.remove(request);
            }
        }
    }
}
