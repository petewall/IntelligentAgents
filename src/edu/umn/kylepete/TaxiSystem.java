package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.ai.TaxiDispatch;
import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.OSRM;
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
		db = new MockTaxiData();
		requestGenerator = new RequestGenerator(db);
		this.vehicles = generateVehicles();
	}

	public void start() {
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

	public static void main(String[] args) throws IOException, ParseException {
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
			Vehicle vehicle = new Vehicle("Vehicle " + (i + 1), "Car", nextRandomPassengerCount(), nextRandomCoordinate());
			vehicles.add(vehicle);
		}
		return vehicles;
	}

	private static Coordinate nextRandomCoordinate() {

		// Here are the min and max coordinates for the January 2013 NYC taxi trips
		//    max_lon  |   min_lon  |  max_lat  | min_lat
		// ------------+------------+-----------+-----------
		//  -73.700096 | -74.049995 | 40.899994 | 40.600021
		final double MAX_LON = -73.700096;
		final double MIN_LON = -74.049995;
		final double MAX_LAT = 40.899994;
		final double MIN_LAT = 40.600021;

		// we want to generate random coordinates within this range but more concentrated around the center
		// to do this we will use a gaussian distribution
		// we also want to make sure the coordinate is a valid road (not in the water) so we will use OSRM

		double lon = nextRandomGaussian(MAX_LON, MIN_LON);
		double lat = nextRandomGaussian(MAX_LAT, MIN_LAT);
		Coordinate coordinate = new Coordinate(lat, lon);
		return OSRM.locate(coordinate);
	}

	private static double nextRandomGaussian(double max, double min) {
		// for a gaussian, 99.7% is within +/-3 standard deviations
		// source: http://stackoverflow.com/questions/2751938/random-number-within-a-range-based-on-a-normal-distribution
		double mean = (max + min) / 2.0;
		double std = (max - min) / 2.0 / 3.0;
		return (randomGenerator.nextGaussian() * std) + mean;
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
