package edu.umn.kylepete;

public class Vehicle {
    private String name;
    private String type;
    private int capacity;
    private Coordinate position;
    private Coordinate destination;
    
    public String toString() {
        return name + "(" + type + ")";
    }
}
