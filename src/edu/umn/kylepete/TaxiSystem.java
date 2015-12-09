package edu.umn.kylepete;

import java.io.IOException;

import edu.umn.kylepete.ai.strategies.AIStrategy;
import edu.umn.kylepete.env.Environment;
import edu.umn.kylepete.env.OSRM;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.stats.RequestStats;
import edu.umn.kylepete.stats.SystemStats;
import edu.umn.kylepete.stats.VehicleStats;

public class TaxiSystem {

	public static void main(String[] args) throws Exception {
	    SystemStats.start();
		TaxiSystemProperties properties = loadProperties(args);

		// get the strategy class first so we flush out any property file/class loading errors
		// before generating the environment (which takes longer)
		AIStrategy aiStrategy = getAIStrategy(properties);

		// setup the environment (random, time, requests, vehicles)
		Environment environment = Environment.getNewEnvironment(properties);

		// register the vehicles to the AI and the AI listeners to the environment
		// TODO may be better design to have new vehicle listeners
		aiStrategy.addVehicles(environment.getVehicles());
		environment.getRequestGenerator().addRequestListeners(aiStrategy.getRequestListeners());

		// run the simulation
		environment.start();

		// Report the final statistics
		for (Vehicle vehicle : environment.getVehicles()) {
			vehicle.reportTimeParked();
		}
		SystemStats.report();
		RequestStats.report();
		VehicleStats.report();
		System.exit(0);
	}

	private static TaxiSystemProperties loadProperties(String[] args) throws IOException {
		if (args.length != 1) {
			if (args.length < 1) {
				System.out.println("Missing property file argument.");
			} else if (args.length > 1) {
				System.out.println("Too many arguments. There must be only one argument for property file.");
			}
			System.out.print("Usage: TaxiSystem path/to/taxisystem.properties");
			System.exit(1);
		}
		TaxiSystemProperties properties = new TaxiSystemProperties(args[0]);
		OSRM.hostname = properties.getOsrmHost();
		OSRM.port = properties.getOsrmPort();
		Logger.debugging = properties.getLoggerDebug();
		return properties;
	}

	private static AIStrategy getAIStrategy(TaxiSystemProperties properties) throws Exception {
		String strategyClassName = properties.getAIStrategy();
		Class<?> strategyClass;
		try {
			strategyClass = Class.forName(strategyClassName);
		} catch (ClassNotFoundException e) {
			Logger.error("TAXI SYSTEM", "Stragegy class with name " + strategyClassName + " could not be found. Make sure the name is fully qualified.");
			throw e;
		}

		Logger.info("TAXI SYSTEM", "Creating AI Stragegy " + strategyClass.getSimpleName());
		Object strategy;
		try {
			strategy = strategyClass.newInstance();
		} catch (Exception e) {
			Logger.error("TAXI SYSTEM", "Unable to instantiate a new AI strategy class of type " + strategyClass.getName());
			throw e;
		}

		if (!(strategy instanceof AIStrategy)) {
			String err = strategyClassName + " class specified in the ai.strategy property must implement the AIStrategy interface.";
			Logger.error("TAXI SYSTEM", err);
			throw new IllegalArgumentException(err);
		}

		return (AIStrategy) strategy;
	}
}
