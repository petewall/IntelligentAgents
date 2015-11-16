package edu.umn.kylepete;

public class NamedCoordinate extends Coordinate {
    public String name;
    
    public NamedCoordinate(String name, double latitude, double longitude) {
        super(latitude, longitude);
        this.name = name;
    }
    
    public NamedCoordinate(String name, Coordinate location) {
        this(name, location.latitude, location.longitude);
    }
}
