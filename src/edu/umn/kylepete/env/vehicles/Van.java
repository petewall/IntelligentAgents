package edu.umn.kylepete.env.vehicles;

import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.EnvironmentTime;

public class Van extends Vehicle {
	public Van(String name, Coordinate startingLocation, EnvironmentTime environmentTime) {
		super(name, startingLocation, environmentTime);
    }

    @Override
    public int getCapacity() {
        return 6;
    }
}
