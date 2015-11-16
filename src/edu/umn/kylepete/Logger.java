package edu.umn.kylepete;

public class Logger {

    public static boolean debugging = true;

    private static void log(String message, boolean error) {
        if (error) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }
    }

    public static void debug(String message) {
        if (debugging)
            log("[DEBUG] " + getMethodName(3) + ": " + message, false);
    }

    public static void debug(String component, String message) {
        if (debugging)
            log("[DEBUG] [" + component + "] " + getMethodName(3) + ": " + message, false);
    }

    public static void info(String message) {
        log("[INFO] " + message, false);
    }

    public static void info(String component, String message) {
        info("[" + component + "] " + message);
    }

    public static void warning(String message) {
        log("[WARNING] " + message, true);
    }

    public static void warning(String component, String message) {
        warning("[" + component + "] " + message);
    }

    public static void error(String message) {
        log("[ERROR] " + message, true);
    }

    public static void error(String component, String message) {
        error("[" + component + "] " + message);
    }
    
    public static String stackTraceToString(Exception e) {
        StringBuilder result = new StringBuilder(e.toString() + System.lineSeparator());
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace)
            result.append("\tat " + traceElement + System.lineSeparator());
        return result.toString();
    }

    private static String getMethodName(final int depth) {
        final boolean recentFirst = true;
        final StackTraceElement[] stes = Thread.currentThread().getStackTrace();

        if (recentFirst) {
            return stes[depth].getMethodName();
        } else {
            return stes[stes.length - 1 - depth].getMethodName();
        }
    }
}
