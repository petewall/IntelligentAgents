package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Random;
import java.util.Set;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.ai.dispatchers.*;
import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.env.vehicles.VehicleFactory;
import edu.umn.kylepete.env.RequestGenerator;
import edu.umn.kylepete.stats.RequestStats;
import edu.umn.kylepete.stats.VehicleStats;

public class TaxiSystem {
	public static Random randomGenerator;
	
	private Set<Vehicle> vehicles;

	private TaxiData db;
	private RequestGenerator requestGenerator;

	public TaxiSystem() {
	    this.db = new MockTaxiData();
		this.requestGenerator = new RequestGenerator(db);
		this.vehicles = VehicleFactory.generateVehicles(randomGenerator);
	}

	public void start() throws EnvironmentTimeException {
		TaxiDispatch dispatch = new SingleAuctionDispatcher();
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

		// Report the final parked time
		for (Vehicle vehicle : vehicles) {
		    vehicle.reportTimeParked();
        }
		RequestStats.report();
		VehicleStats.report();
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

	public static void main(String[] args) throws IOException, ParseException, EnvironmentTimeException {
		if (args.length != 1) {
			if (args.length < 1) {
				System.out.println("Missing property file argument.");
			} else if (args.length > 1) {
				System.out.println("Too many arguments. There must be only one argument for property file.");
			}
			System.out.print("Usage: TaxiSystem path/to/taxisystem.properties");
			System.exit(1);
		}
		TaxiSystemProperties.loadProperties(args[0]);
		Long randomSeed = TaxiSystemProperties.getRandomSeed();
		if(randomSeed != null){
			randomGenerator = new Random(randomSeed);
		}else{
			randomGenerator = new Random();
		}
		EnvironmentTime.initializeTime(TaxiSystemProperties.getTimeStart());
		TaxiSystem system = new TaxiSystem();
		system.start();
	}
}
