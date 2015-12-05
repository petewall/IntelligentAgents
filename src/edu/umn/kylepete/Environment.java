package edu.umn.kylepete;

import java.text.ParseException;
import java.util.Random;
import java.util.Set;

import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.EnvironmentTime;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.env.RequestGenerator;
import edu.umn.kylepete.env.vehicles.Vehicle;
import edu.umn.kylepete.env.vehicles.VehicleFactory;

public class Environment {

	private Random randomGenerator;
	private EnvironmentTime environmentTime;
	private RequestGenerator requestGenerator;
	private Set<Vehicle> vehicles;

	private Environment() {
	}

	public static Environment getNewEnvironment(TaxiSystemProperties properties) throws ParseException, EnvironmentTimeException {
		Environment env = new Environment();

		Long randomSeed = properties.getRandomSeed();
		if (randomSeed != null) {
			env.randomGenerator = new Random(randomSeed);
		} else {
			env.randomGenerator = new Random();
		}
		env.environmentTime = new EnvironmentTime();
		env.environmentTime.initializeTime(properties.getTimeStart());

		TaxiData db = new MockTaxiData();
		env.requestGenerator = new RequestGenerator(db);
		env.requestGenerator.generateRequests(env.environmentTime);
		env.vehicles = VehicleFactory.generateVehicles(properties.getTaxiCount(), env);
		return env;
	}

	public void start() {
		while (true) {
			if (!this.getTime().advanceTime()) {
				break;
			}
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
