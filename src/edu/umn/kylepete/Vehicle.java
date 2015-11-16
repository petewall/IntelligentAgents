package edu.umn.kylepete;

import edu.umn.kylepete.Requests.NoRequestsException;

public class Vehicle extends Thread {
    private String type;
    private int capacity;
    private boolean running;
    private Coordinate currentLocation;
    private Request activeRequest;
    
    public Vehicle(String name, String type, int capacity) {
        super(name);
        this.type = type;
        this.capacity = capacity;
    }
    
    public void run() {
        running = true;
        while (running) {
            if (activeRequest == null) {
                try {
                    activeRequest = Requests.assignNearestToMe(this);
                } catch (NoRequestsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
