package edu.umn.kylepete.stats;

public class RequestStats {
    private static int fulfilled = 0;
    
    private static long idleTime = 0;
    private static long maxIdle = 0;
    private static long minIdle = Long.MAX_VALUE; 
    private static int idleCount = 0;
    
    public static void requestFulfilled() {
        fulfilled++;
    }
    
    public static void addIdleTime(long idleSeconds) {
        idleCount++;
        idleTime += idleSeconds;
        if (idleSeconds > maxIdle) {
            maxIdle = idleSeconds;
        }
        if (idleSeconds < minIdle) {
            minIdle = idleSeconds;
        }
    }
    
    public static void report() {
        System.out.println("Fulfilled: " + fulfilled);
        if (idleCount > 0) {
            System.out.println("Idle: " + idleTime + " seconds.  MAX(" + maxIdle + ") MIN(" + minIdle + ") AVG(" + (idleTime / idleCount) + ")");
        }
    }
}
