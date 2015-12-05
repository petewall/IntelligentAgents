package edu.umn.kylepete.ai.dispatchers;

import java.util.ArrayList;
import java.util.List;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class NaiveDispatcher extends TaxiDispatch {

    private List<TaxiAgent> waitingTaxis;
    private List<TaxiAgent> busyTaxis;
    
    public NaiveDispatcher() {
        waitingTaxis = new ArrayList<TaxiAgent>();
        busyTaxis = new ArrayList<TaxiAgent>();
    }
    
    @Override
    public void addTaxi(TaxiAgent taxi) {
        waitingTaxis.add(taxi);
    }
        
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
