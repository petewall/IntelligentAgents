package edu.umn.kylepete.ai.dispatchers;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.env.Coordinate;
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
    
    private TaxiAgent findNearestIdleTaxi(Coordinate locaiton) {
        TaxiAgent nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (TaxiAgent agent : waitingTaxis) {
            double distance = agent.getVehicle().getLocation().distance(locaiton);
            if (distance < nearestDistance) {
                nearest = agent;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private void processRequests(){
        while(waitingTaxis.size() > 0 && requestQueue.size() > 0){
            Request request = requestQueue.poll();
            TaxiAgent agent = findNearestIdleTaxi(request.getPickupLocation());
            waitingTaxis.remove(agent);
            busyTaxis.add(agent);
            agent.fulfillRequest(request);
        }
    }
}
