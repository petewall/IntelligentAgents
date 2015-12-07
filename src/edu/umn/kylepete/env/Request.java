package edu.umn.kylepete.env;

import java.util.Date;

public class Request {
    private Date time;
    private Coordinate pickup;
    private Coordinate dropoff;
    private double distance;
    private int passengers;
    
    public Request(Date time, Coordinate pickup, Coordinate dropoff, int passengers) {
        this.time = time;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.distance = pickup.distance(dropoff); // FIXME Should this be made truly accurate using OSRM? 
        this.passengers = passengers;
    }
    
    public Date getTime() {
        return time;
    }

    public Coordinate getPickupLocation() {
        return pickup;
    }
    
    public Coordinate getDropoffLocation() {
        return dropoff;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public int getNumberOfPassengers() {
        return passengers;
    }
    
    public String toString() {
        return "Request: " + passengers + " passenger(s) from " + pickup + " to " + dropoff;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dropoff == null) ? 0 : dropoff.hashCode());
		result = prime * result + passengers;
		result = prime * result + ((pickup == null) ? 0 : pickup.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (dropoff == null) {
			if (other.dropoff != null)
				return false;
		} else if (!dropoff.equals(other.dropoff))
			return false;
		if (passengers != other.passengers)
			return false;
		if (pickup == null) {
			if (other.pickup != null)
				return false;
		} else if (!pickup.equals(other.pickup))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

}
