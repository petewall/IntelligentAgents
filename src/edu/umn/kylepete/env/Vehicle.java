package edu.umn.kylepete.env;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.RequestService;
import edu.umn.kylepete.RequestService.NoRequestsException;

public class Vehicle implements RequestListener {
    private String name;
    private int capacity;
    private Coordinate currentLocation;
    private Request activeRequest;
    private Status status;
    private long currentTime;
    private long expectedTime;

    public enum Status {
        WAITING,    // Waiting for a request
        PICKING_UP, // Driving to pick up a request
        DRIVING     // Driving a request to the destination
    }

    public Vehicle(String name, String type, int capacity, Coordinate startingLocation) {
        this.name = name;
        this.capacity = capacity;
        this.currentLocation = startingLocation;
        this.status = Status.WAITING;
        RequestService.getInstance().addRequestListener(this);
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    private void driveToRequest() {
        status = Status.PICKING_UP;
        Route route = OSRM.viaRoute(currentLocation, activeRequest.getPickupLocation());
        expectedTime = currentTime + route.time;
    }

    private void deliverRequest() {
        status = Status.DRIVING;
        Route route = OSRM.viaRoute(currentLocation, activeRequest.getDropoffLocation());
        expectedTime = currentTime + route.time;
    }

    private void findRequest() {
        try {
            status = Status.WAITING;
            Request newRequest = RequestService.getInstance().assignNearestToMe(this);
            activeRequest = newRequest;
            driveToRequest();
        } catch (NoRequestsException e) {
            Logger.debug("Vehicle " + this.getName(), "No requests available");
        }
    }

    private void advanceClock(long newTime) {
        if (status == Status.PICKING_UP) {
            if (newTime > expectedTime) {
                currentTime = expectedTime;
                currentLocation = activeRequest.getPickupLocation();
                deliverRequest();
            }
        }
        
        if (status == Status.DRIVING) {
            if (newTime > expectedTime) {
                currentTime = expectedTime;
                currentLocation = activeRequest.getDropoffLocation();
                activeRequest = null;
                findRequest();
                advanceClock(newTime);
            }
        }
        currentTime = newTime;
    }

    public void newRequest(Request newRequest) {
        advanceClock(newRequest.getTime().getTime());
        switch (this.status) {
        case WAITING:
            try {
                RequestService.getInstance().assignRequest(newRequest, this);
                activeRequest = newRequest;
                driveToRequest();
            } catch (NoRequestsException e) {
                Logger.debug("Vehicle " + this.getName(), "Somebody else must have picked up this request");
            }
            return;
        case PICKING_UP:
            return;
        case DRIVING:
            return;
        }
    }

    public Status getStatus() {
        return this.status;
    }
    
    public long getTime() {
        return currentTime;
    }
    
    public String toString() {
        return this.getName() + " (" + status + ")";
    }

    public Coordinate getLocation() {
        return this.currentLocation;
    }
}
