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
}
