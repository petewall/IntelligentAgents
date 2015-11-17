package edu.umn.kylepete;

import edu.umn.kylepete.RequestService.NoRequestsException;

public class Vehicle extends Thread {
    private String type;
    private int capacity;
    private Environment environment;
    private boolean running;
    private Coordinate currentLocation;
    private Request activeRequest;
    
    public Vehicle(String name, String type, int capacity, Environment environment) {
        super(name);
        this.type = type;
        this.capacity = capacity;
        this.environment = environment;
    }
    
    public int getCapacity() {
        return this.capacity;
    }

    public void run() {
        running = true;
        while (running) {
            if (activeRequest == null) {
                try {
                    activeRequest = RequestService.assignNearestToMe(this);
                } catch (NoRequestsException e) {
                    Logger.debug("Vehicle " + this.getName(), "No requests available");
                }
            }
        }
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
