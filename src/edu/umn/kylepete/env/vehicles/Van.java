package edu.umn.kylepete.env.vehicles;

import edu.umn.kylepete.env.Coordinate;

public class Van extends Vehicle {
    public Van(String name, Coordinate startingLocation) {
        super(name, startingLocation);
    }

    @Override
    public int getCapacity() {
        return 6;
    }
}
