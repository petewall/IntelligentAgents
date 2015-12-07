package edu.umn.kylepete.env;

import java.text.ParseException;
import java.util.Random;
import java.util.Set;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.env.vehicles.VehicleFactory;
import edu.umn.kylepete.stats.RequestStats;

public class Environment {

	private Random randomGenerator;
	private EnvironmentTime environmentTime;
	private RequestGenerator requestGenerator;
	private Set<Vehicle> vehicles;

	private Environment() {
	}

	public static Environment getNewEnvironment(TaxiSystemProperties properties) throws ParseException, EnvironmentTimeException {
		Logger.info("ENVIRONMENT", "Creating new taxi simulation environment");
		Environment env = new Environment();

		Long randomSeed = properties.getRandomSeed();
		if (randomSeed != null) {
			env.randomGenerator = new Random(randomSeed);
		} else {
			env.randomGenerator = new Random();
		}
		env.environmentTime = new EnvironmentTime();
		env.environmentTime.initializeTime(properties.getTimeStart());

		env.requestGenerator = new RequestGenerator(properties);
		env.vehicles = VehicleFactory.generateVehicles(properties.getTaxiCount(), env);
		return env;
	}

	public void start() throws InterruptedException {
		requestGenerator.start(environmentTime);
		Logger.info("ENVIRONMENT", "Starting time simulation");
		int count = 0;
		while (true) {
			// don't get ahead of the requestGenerator
			while (requestGenerator.getProcessingTime() != null && environmentTime.getNextTime().compareTo(requestGenerator.getProcessingTime()) >= 0) {
				Logger.info("ENVIRONMENT", "Waiting for request generator");
				Thread.sleep(1000);
			}
			if (!this.getTime().advanceTime()) {
				break;
			}
			if (count % 60 == 0) {
				Logger.info("ENVIRONMENT", "Time: " + this.getTime().getCurTime() + " Requests fulfilled: " + RequestStats.getRequestsFulfilled());
			}
			count++;
		}
	}

	public Random getRandomGenerator() {
		return randomGenerator;
	}

	public EnvironmentTime getTime() {
		return environmentTime;
	}

	public RequestGenerator getRequestGenerator() {
		return requestGenerator;
	}

	public Set<Vehicle> getVehicles() {
		return vehicles;
	}


}
