package edu.umn.kylepete.ai.dispatchers;

import java.util.ArrayList;
import java.util.List;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class DistanceDispatcher extends TaxiDispatch {

    private List<TaxiAgent> waitingTaxis;
    private List<TaxiAgent> busyTaxis;
    
    public DistanceDispatcher() {
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
    public void requestComplete(TaxiAgent taxiAgent, Request completedRequest) {
        busyTaxis.remove(taxiAgent);
        waitingTaxis.add(taxiAgent);
        processRequests();
    }

    private TaxiAgent findNearestIdleTaxi(Request request) {
        TaxiAgent nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (TaxiAgent agent : waitingTaxis) {
            if (agent.getVehicle().getCapacity() >= request.getNumberOfPassengers()) {
                double distance = agent.getVehicle().getLocation().distance(request.getPickupLocation());
                if (distance < nearestDistance) {
                    nearest = agent;
                    nearestDistance = distance;
                }
            }
        }
        return nearest;
    }

    private void processRequests(){
        Request[] requests = requestQueue.toArray(new Request[requestQueue.size()]);
        for (Request request : requests) {
            TaxiAgent agent = findNearestIdleTaxi(request);
            if (agent != null) {
                waitingTaxis.remove(agent);
                busyTaxis.add(agent);
                agent.assignRequest(request);
                requestQueue.remove(request);
            }
        }
    }
}
