package edu.umn.kylepete.env;

import java.util.Random;

/**
 * Encapsulates a latitude and a longitudes as a coordinate.
 * @author pwall
 */
public class Coordinate {
    /**
     * The coordinate's latitude.  Positive is North, negative is South.
     */
    public double latitude;
    
    /**
     * The coordinate's longitude.  Positive is East, negative is West.
     */
    public double longitude;

    /**
     * The constructor that sets the latitude and longitude
     * @param latitude The latitude for the coordinate
     * @param longitude The longitude for the coordinate
     */
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
	public Coordinate(String latitude, String longitude) {
		this.latitude = Double.parseDouble(latitude);
		this.longitude = Double.parseDouble(longitude);
	}

    public static Coordinate getRandomCoordinate(Random randomGenerator) {
        // Here are the min and max coordinates for the January 2013 NYC taxi trips
        //    max_lon  |   min_lon  |  max_lat  | min_lat
        // ------------+------------+-----------+-----------
        //  -73.700096 | -74.049995 | 40.899994 | 40.600021
        final double MAX_LON = -73.700096;
        final double MIN_LON = -74.049995;
        final double MAX_LAT = 40.899994;
        final double MIN_LAT = 40.600021;

        // we want to generate random coordinates within this range but more concentrated around the center
        // to do this we will use a gaussian distribution
        // we also want to make sure the coordinate is a valid road (not in the water) so we will use OSRM

        double lon = nextRandomGaussian(randomGenerator, MAX_LON, MIN_LON);
        double lat = nextRandomGaussian(randomGenerator, MAX_LAT, MIN_LAT);
        Coordinate coordinate = new Coordinate(lat, lon);
		// for performance reasons we won't "locate" a random coordinate
		// this helps speed up vehicle generation but means a vehicle may start in water
		// but the first OSRM call for the vehicle to drive somewhere will correct this
		// coordinate = OSRM.locate(coordinate);
        return coordinate;
    }

    private static double nextRandomGaussian(Random randomGenerator, double max, double min) {
        // for a gaussian, 99.7% is within +/-3 standard deviations
        // source: http://stackoverflow.com/questions/2751938/random-number-within-a-range-based-on-a-normal-distribution
        double mean = (max + min) / 2.0;
        double std = (max - min) / 2.0 / 3.0;
        return (randomGenerator.nextGaussian() * std) + mean;
    }

    /**
     * 
     */
    public String toString() {
        return (latitude > 0 ? latitude + "N " : -1 * latitude + "S ") +
               (longitude > 0 ? longitude + "E" : -1 * longitude + "W");
    }

    /**
     * Returns the distance, in meters, to another coordinate.  This
     * uses the "haversine" method to calculate distances on a sphere.
     * From http://www.movable-type.co.uk/scripts/latlong.html
     * This is the straight-line distance, not a driving distance.
     * @param other The other coordinate.
     * @return The distnance, in meters, to the other coordinate.
     */
    public double distance(Coordinate other) {
        int R = 6371000; // metres
        double φ1 = Math.toRadians(this.latitude);
        double φ2 = Math.toRadians(other.latitude);
        double Δφ = Math.toRadians(other.latitude - this.latitude);
        double Δλ = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Checks the equality of two coordinates with precision of about 1 meter.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (Math.abs(latitude - other.latitude) > 0.00001)
			return false;
        if (Math.abs(longitude - other.longitude) > 0.00001)
			return false;
		return true;
	}

}
