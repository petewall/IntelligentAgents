package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class TaxiSystem {
    private Set<Vehicle> vehicles;

    /**
     * Eventually, we may want to run this dynamically
     */
    private int numberOfVehicles = 4;
    
    public TaxiSystem() {
        this.vehicles = new HashSet<Vehicle>();
        for (int i = 0; i < numberOfVehicles; ++i) {
            Vehicle vehicle = new Vehicle("Vehicle " + (i + 1), "Car", 4, new Coordinate(40.748433, -73.985656));
            this.vehicles.add(vehicle);
        }
    }
    
    public void start() {
        for (Vehicle vehicle : vehicles) {
            vehicle.startSimulation();
        }
        while (true) {
            printState();
            String command = inputString();
            if (command.equals("q")) {
                stop();
                System.exit(0);
            }
            RequestService.getInstance().getNext();
        }
    }
    
    public void stop() {
        for (Vehicle vehicle : vehicles) {
            vehicle.stopSimulating();
        }
    }

    private void printState() {
        System.out.println(RequestService.getInstance().toString());
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle.toString());
        }
    }

    public static String inputString() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        
        try {
            return input.readLine();
        } catch (IOException e) {
            Logger.warning(Logger.stackTraceToString(e));
            return inputString();
        }
    }
    
    public static void main(String[] args) {
        TaxiSystem system = new TaxiSystem();
        system.start();
    }
}
