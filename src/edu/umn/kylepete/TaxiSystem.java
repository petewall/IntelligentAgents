package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.ai.dispatchers.*;
import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.env.RequestGenerator;
import edu.umn.kylepete.env.Vehicle;
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
		this.vehicles = generateVehicles();
	}

	public void start() throws EnvironmentTimeException {
		TaxiDispatch dispatch = new DistanceDispatcher();
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

	private Set<Vehicle> generateVehicles() {
		int count = TaxiSystemProperties.getTaxiCount();
		Logger.info("Generating " + count + " vehicles");
		Set<Vehicle> vehicles = new HashSet<Vehicle>();

		for (int i = 0; i < count; ++i) {
			Vehicle vehicle = new Vehicle("Vehicle " + (i + 1), "Car", nextRandomPassengerCount(), Coordinate.getRandomCoordinate(randomGenerator));
			vehicles.add(vehicle);
		}
		return vehicles;
	}
	private static int nextRandomPassengerCount() {
		// I found online that most taxis are required by law to only hold 4 passengers
		// A few exceptions exist for 5 passenger minivan cabs and/or for young children
		// http://www.nyc.gov/html/tlc/html/faq/faq_pass.shtml#3
		// I couldn't find an exact percentage of the fleet that is minivans, but
		// many people eluded they are currently less than 10%
		// I'm going to go with 80% 4 passenger and 20% 6 passenger to estimate the exceptions

		if (randomGenerator.nextDouble() < 0.80) {
			return 4;
		} else {
			return 6;
		}
	}
}
