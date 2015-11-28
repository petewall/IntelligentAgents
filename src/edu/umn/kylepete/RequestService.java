package edu.umn.kylepete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.db.TaxiData;

public class RequestService {
    @SuppressWarnings("serial")
    public static class NoRequestsException extends Exception {};

    private TaxiData db;
    private Set<Request> activeRequests;
    private Set<RequestListener> listeners;
    private static RequestService instance = null;

    private RequestService() {
        this.db = new MockTaxiData();
        this.activeRequests = new HashSet<Request>();
        this.listeners = new HashSet<RequestListener>();
    }

    public static synchronized RequestService getInstance() {
        if (instance == null) {
            instance = new RequestService();
        }
        return instance;
    }
    
    public void addRequestListener(RequestListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeRequestListener(RequestListener listener) {
        this.listeners.remove(listener);
    }
    
    public synchronized Request getNearest(Coordinate position) {
        return activeRequests.iterator().next();
    }
    
    public synchronized Request assignRequest(Request request, Vehicle vehicle) throws NoRequestsException {
        if (activeRequests.contains(request)) {
            activeRequests.remove(request);
            return request;
        }
        throw new NoRequestsException();
    }

    public synchronized Request getNearestToMe(Vehicle vehicle) throws NoRequestsException {
        if (activeRequests.size() == 0) {
            throw new NoRequestsException();
        }
        if (activeRequests.size() == 1) {
            return activeRequests.iterator().next();
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

    public synchronized Request assignNearestToMe(Vehicle vehicle) throws NoRequestsException {
        return assignRequest(getNearestToMe(vehicle), vehicle);
    }
    
    private void notifyListeners(Request request) {
        for (RequestListener listener : listeners) {
            listener.newRequest(request);
        }
    }

    public long getNext() {
        Request request = db.getNextRequest();
        activeRequests.add(request);
        notifyListeners(request);
        return request.getTime().getTime();
    }

    public String toString() {
        return "Requests: active(" + activeRequests.size() + ")";
    }
}
