package edu.umn.kylepete.env.vehicles;

import edu.umn.kylepete.env.Coordinate;

public interface VehicleListener {
    public void arriveAtLoc(Vehicle vehicle, Coordinate loc);
}
