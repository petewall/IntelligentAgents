package edu.umn.kylepete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestService {
    @SuppressWarnings("serial")
    public static class NoRequestsException extends Exception {};
    
    private static Set<Request> activeRequests = new HashSet<Request>();
    private static Map<Vehicle, Request> assignedRequests = new HashMap<Vehicle, Request>();

    public synchronized static Request getNearest(Coordinate position) {
        return activeRequests.iterator().next();
    }
    
    public synchronized static Request assignRequest(Request request, Vehicle vehicle) {
        activeRequests.remove(request);
        assignedRequests.put(vehicle, request);
        return request;
    }
    
    public synchronized static Request getNearestToMe(Vehicle vehicle) throws NoRequestsException {
        if (activeRequests.size() == 0) {
            throw new NoRequestsException();
        }
        if (activeRequests.size() == 1) {
            return assignRequest(activeRequests.iterator().next(), vehicle);
        }
        if (activeRequests.size() == 0) {
            return null;
        }
        if (activeRequests.size() == 1) {
            return assignRequest(activeRequests.iterator().next(), vehicle);
        }

        Request[] requests = (Request[]) activeRequests.toArray();
        List<Coordinate> locations = new ArrayList<Coordinate>();
        locations.add(vehicle.getLocation());
        for (Request request : requests) {
            locations.add(request.getPickupLocation());
        }

        List<Integer> distances = OSRM.table((Coordinate[]) locations.toArray()).get(0);
        int smallestIndex = 1;
        Integer smallestDistance = distances.get(1);
        for (int i = 2; i < distances.size(); i += 1) {
            if (distances.get(i).compareTo(smallestDistance) < 0) {
                smallestIndex = i;
                smallestDistance = distances.get(i);
            }
        }
        return requests[smallestIndex];
    }

    public synchronized static Request assignNearestToMe(Vehicle vehicle) throws NoRequestsException {
        return assignRequest(getNearestToMe(vehicle), vehicle);
    }
}
