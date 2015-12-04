package edu.umn.kylepete.ai.dispatchers;

import java.util.PriorityQueue;
import java.util.Queue;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.auctions.Bid;
import edu.umn.kylepete.env.Request;

/**
 * The SingleAuctionDispatcher runs an auction for a single request.
 * @author pwall
 *
 */
public class SingleAuctionDispatcher extends TaxiDispatch {
        
    @Override
    public void newRequest(Request event) {
        requestQueue.add(event);
        processRequests();
    }

    @Override
    public void requestComplete(TaxiAgent taxiAgent) {
        busyTaxis.remove(taxiAgent);
        waitingTaxis.add(taxiAgent);
        processRequests();
    }

    private void processRequests() {
        Request[] requests = requestQueue.toArray(new Request[requestQueue.size()]);
        for (Request request : requests) {
            Queue<Bid> bids = new PriorityQueue<Bid>();
            for (TaxiAgent agent : waitingTaxis) {
                Bid bid = agent.getBid(request);
                if (!bid.abstain) {
                    bids.add(bid);
                }
            }

            if (bids.size() > 0) {
                Bid winningBid = bids.remove();
                waitingTaxis.remove(winningBid.bidder);
                busyTaxis.add(winningBid.bidder);
                winningBid.bidder.fulfillRequest(request);
                requestQueue.remove(request);
            }
        }
    }
}
