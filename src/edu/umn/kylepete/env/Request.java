package edu.umn.kylepete.env;

import java.util.Date;

public class Request {
	private Date submitTime;
	private int passengers;
	private Route route;
    
	public Request(Date requestSubmitTime, int passengers, Route route) {
		this.submitTime = requestSubmitTime;
		this.passengers = passengers;
		this.route = route;
		if (route == null) {
			throw new IllegalArgumentException("route argument must not be null");
		}
	}

	public Request(Date requestTime, int passengers, Coordinate pickup, Coordinate dropoff) {
		this(requestTime, passengers, OSRM.viaRoute(pickup, dropoff));
    }
    
	public Date getSubmitTime() {
		return submitTime;
    }

    public Coordinate getPickupLocation() {
		return getRoute().getStartCoordinate();
    }
    
    public Coordinate getDropoffLocation() {
		return getRoute().getEndCoordinate();
    }
    
    public double getDistance() {
		return getRoute().getDistance();
	}

	public int getTravelTime() {
		return getRoute().getTime();
    }

	public Route getRoute() {
		return route;
	}
    
    public int getNumberOfPassengers() {
        return passengers;
    }
    
    public String toString() {
		return "Request: " + passengers + " passenger(s) from " + getPickupLocation() + " to " + getDropoffLocation();
    }

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (passengers != other.passengers)
			return false;
		if (submitTime == null) {
			if (other.submitTime != null)
				return false;
		} else if (!submitTime.equals(other.submitTime))
			return false;

		if (!getDropoffLocation().equals(other.getDropoffLocation()))
			return false;

		if (!getPickupLocation().equals(other.getPickupLocation()))
			return false;

		return true;
	}


}
