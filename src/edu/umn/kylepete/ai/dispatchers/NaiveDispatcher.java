package edu.umn.kylepete.ai.dispatchers;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class NaiveDispatcher extends TaxiDispatch {
        
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
            for (TaxiAgent agent : waitingTaxis) {
                if (agent.getVehicle().getCapacity() >= request.getNumberOfPassengers()) {
                    waitingTaxis.remove(agent);
                    busyTaxis.add(agent);
                    agent.fulfillRequest(request);
                    requestQueue.remove(request);
                    break;
                }
            }
        }
    }
}
