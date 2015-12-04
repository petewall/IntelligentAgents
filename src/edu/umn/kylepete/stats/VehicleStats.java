package edu.umn.kylepete.stats;

public class VehicleStats {
    private static int metersDriven;
    private static int maxDistance = 0;
    private static int minDistance = Integer.MAX_VALUE;

    private static long drivingTime = 0;
    private static long maxDriving = 0;
    private static long minDriving = Long.MAX_VALUE; 
    private static int drivingCount = 0;
    
    private static long parkedTime = 0;
    private static long maxParked = 0;
    private static long minParked = Long.MAX_VALUE; 
    private static int parkedCount = 0;

    public static void addDrive(int distance, long time) {
        drivingCount++;

        metersDriven += distance;
        if (distance > maxDistance) {
            maxDistance = distance;
        }
        if (distance < minDistance) {
            minDistance = distance;
        }

        drivingTime += time;
        if (time > maxDriving) {
            maxDriving = time;
        }
        if (time < minDriving) {
            minDriving = time;
        }
    }

    public static void addParked(long time) {
        parkedCount++;
        parkedTime += time;
        if (time > maxParked) {
            maxParked = time;
        }
        if (time < minParked) {
            minParked = time;
        }
    }

    public static void report() {
        System.out.println("Vehicle Stats:");
        if (drivingCount > 0) {
            System.out.println("Driving distance: " + metersDriven + " meters.  MAX(" + maxDistance + ") MIN(" + minDistance + ") AVG(" + (metersDriven / drivingCount) + ")");
            System.out.println("Driving time: " + drivingTime + " seconds.  MAX(" + maxDriving + ") MIN(" + minDriving + ") AVG(" + (drivingTime / drivingCount) + ")");
        }
        if (parkedCount > 0) {
            System.out.println("Parked: " + parkedTime + " seconds.  MAX(" + maxParked + ") MIN(" + minParked + ") AVG(" + (parkedTime / parkedCount) + ")");
        }

    }
}
