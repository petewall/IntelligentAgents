package edu.umn.kylepete;

import java.util.HashSet;
import java.util.Set;

public class TaxiSystem {
    private Environment environment;
    private Set<Vehicle> vehicles;
    
    /**
     * Eventually, we may want to run this dynamically
     */
    private int numberOfVehicles = 5;
    
    public TaxiSystem() {
        this.environment = new Environment();
        this.vehicles = new HashSet<Vehicle>();
        for (int i = 0; i < numberOfVehicles; ++i) {
            Vehicle vehicle = new Vehicle("Vehicle " + (i + 1), "Car", 4, environment);
            this.vehicles.add(vehicle);
        }
    }
    
    public void start() {
        for (Vehicle vehicle : vehicles) {
            vehicle.startSimulation();
        }
    }
    
    public static void main(String[] args) {
        TaxiSystem system = new TaxiSystem();
        //system.start();
    }
}
