package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.ai.TaxiDispatch;
import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.RequestGenerator;
import edu.umn.kylepete.env.Vehicle;
import edu.umn.kylepete.stats.RequestStats;

public class TaxiSystem {
    private Set<Vehicle> vehicles;

    /**
     * Eventually, we may want to run this dynamically
     */
    private int numberOfVehicles = 50;
    private TaxiData db;
    private RequestGenerator requestGenerator;
    
    public TaxiSystem() {
        db = new MockTaxiData();
        requestGenerator = new RequestGenerator(db);
        this.vehicles = new HashSet<Vehicle>();
        for (int i = 0; i < numberOfVehicles; ++i) {
            Vehicle vehicle = new Vehicle("Vehicle " + (i + 1), "Car", 4, new Coordinate(40.748433, -73.985656));
            this.vehicles.add(vehicle);
        }
    }
    
    public void start() {
    	EnvironmentTime.initializeTime(new Date(db.getStartTime() - 2L));
    	TaxiDispatch dispatch = new TaxiDispatch();
    	for (Vehicle vehicle : vehicles) {
    		dispatch.addTaxi(new TaxiAgent(vehicle, dispatch));
    	}
    	requestGenerator.addRequestListener(dispatch);
    	requestGenerator.generateRequests();
    	
        while (true) {
            if (!EnvironmentTime.advanceTime()) {
                break;
            }
        }
        RequestStats.report();
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
