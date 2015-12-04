package edu.umn.kylepete.env.vehicles;

import edu.umn.kylepete.env.Coordinate;

public class Car extends Vehicle {
    public Car(String name, Coordinate startingLocation) {
        super(name, "Car", 4, startingLocation);
    }
}
