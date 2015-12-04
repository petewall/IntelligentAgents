package edu.umn.kylepete.ai.dispatchers;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.env.Request;

public class DistanceDispatcher extends TaxiDispatch {

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
            } else {
                System.out.println("stop me");
            }
        }
        return nearest;
    }

    private void processRequests(){
        while(waitingTaxis.size() > 0 && requestQueue.size() > 0){
            Request request = requestQueue.poll();
            TaxiAgent agent = findNearestIdleTaxi(request);
            if (agent != null) {
                waitingTaxis.remove(agent);
                busyTaxis.add(agent);
                agent.fulfillRequest(request);
            }
        }
    }
}
