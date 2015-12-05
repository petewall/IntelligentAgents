package edu.umn.kylepete.ai.strategies;

import java.util.Collection;

import edu.umn.kylepete.env.RequestListener;
import edu.umn.kylepete.env.vehicles.Vehicle;

public interface AIStrategy {

	public void addVehicles(Collection<Vehicle> vehicles);

	public Collection<? extends RequestListener> getRequestListeners();
}
