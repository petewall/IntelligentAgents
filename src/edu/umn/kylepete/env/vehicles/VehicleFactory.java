package edu.umn.kylepete.env.vehicles;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.env.Coordinate;

public class VehicleFactory {
    
    public static Set<Vehicle> generateVehicles(Random randomGenerator) {
        int count = TaxiSystemProperties.getTaxiCount();
        Logger.info("VEHICLE FACTORY", "Generating " + count + " vehicles");
        Set<Vehicle> vehicles = new HashSet<Vehicle>();

        for (int i = 0; i < count; ++i) {
            vehicles.add(makeVehicle(randomGenerator, "Vehicle " + (i + 1), Coordinate.getRandomCoordinate(randomGenerator)));
        }
        return vehicles;
    }
    private static Vehicle makeVehicle(Random randomGenerator, String name, Coordinate startingLocaiton) {
        // I found online that most taxis are required by law to only hold 4 passengers
        // A few exceptions exist for 5 passenger minivan cabs and/or for young children
        // http://www.nyc.gov/html/tlc/html/faq/faq_pass.shtml#3
        // I couldn't find an exact percentage of the fleet that is minivans, but
        // many people eluded they are currently less than 10%
        // I'm going to go with 80% 4 passenger and 20% 6 passenger to estimate the exceptions

        if (randomGenerator.nextDouble() < 0.80) {
            return new Car(name, startingLocaiton);
        } else {
            return new Van(name, startingLocaiton);
        }
    }
}
