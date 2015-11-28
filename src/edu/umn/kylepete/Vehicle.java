package edu.umn.kylepete;

import edu.umn.kylepete.RequestService.NoRequestsException;

public class Vehicle extends Thread implements RequestListener {
    private String type;
    private int capacity;
    private boolean running;
    private Coordinate currentLocation;
    private Request activeRequest;
    private Status state;
    
    public enum Status {
        WAITING,    // Waiting for a request
        PICKING_UP, // Driving to pick up a request
        DRIVING     // Driving a request to the destination
    }
    
    public Vehicle(String name, String type, int capacity, Coordinate startingLocation) {
        super(name);
        this.type = type;
        this.capacity = capacity;
        this.currentLocation = startingLocation;
        RequestService.getInstance().addRequestListener(this);
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void run() {
        running = true;
        state = Status.WAITING;
        while (running) {
            if (activeRequest == null) {
                try {
                    Request newRequest = RequestService.getInstance().assignNearestToMe(this);
                    if (newRequest == null) {
                        state = Status.WAITING;
                    } else {
                        activeRequest = newRequest;
                        state = Status.PICKING_UP;
                    }
                } catch (NoRequestsException e) {
                    Logger.debug("Vehicle " + this.getName(), "No requests available");
                }
            }
        }
    }

    public void newRequest(RequestEvent event) {
        
    }
    
    public void startSimulation() {
        this.start();
    }
    
    public void stopSimulating() {
        running = false;
    }
    
    public String toString() {
        return this.getName() + "(" + type + ")";
    }

    public Coordinate getLocation() {
        return this.currentLocation;
    }
}
