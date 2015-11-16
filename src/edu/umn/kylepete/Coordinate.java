package edu.umn.kylepete;

public class Coordinate {
    public double latitude;
    public double longitude;
    
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return (latitude > 0 ? latitude + "N " : -1 * latitude + "S ") +
               (longitude > 0 ? longitude + "E" : -1 * longitude + "W");
    }

    public double distance(Coordinate other) {
        // From http://www.movable-type.co.uk/scripts/latlong.html
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
