package edu.umn.kylepete.env;

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
}
