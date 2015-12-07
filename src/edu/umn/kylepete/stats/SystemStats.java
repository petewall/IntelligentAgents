package edu.umn.kylepete.stats;

import java.util.Date;

public class SystemStats {
    private static Date startTime;
    
    public static void start() {
        startTime = new Date(); 
    }
    
    public static String msToTime(long ms) {
        long seconds = ms/1000;
        
        long hours = seconds / 3600;
        seconds = seconds % 3600;
        long minutes = seconds / 60; 
        seconds = seconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public static void report() {
        System.out.println("Test run length: " + msToTime(new Date().getTime() - startTime.getTime()));
    }
}
