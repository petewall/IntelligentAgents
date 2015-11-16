package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class OSRM {
    private static String hostname = "192.168.0.100";
    private static int port = 5000;
    
    public static void locate(Coordinate location) {
        sendRequest("locate?loc=" + location.latitude + "," + location.longitude);
    }
    
    public static void nearest(Coordinate location) {
        sendRequest("nearest?loc=" + location.latitude + "," + location.longitude);
    }
    
    public static void viaRoute(Coordinate[] locations) {
        StringBuilder request = new StringBuilder("viaroute?");
        for (Coordinate location : locations) {
            request.append("loc=" + location.latitude + "," + location.longitude + "&");
        }
        sendRequest(request.toString());
    }
    
    public static void table(Coordinate[] locations) {
        StringBuilder request = new StringBuilder("table?");
        for (Coordinate location : locations) {
            request.append("loc=" + location.latitude + "," + location.longitude + "&");
        }
        sendRequest(request.toString());
    }

    public static void match() {
        // FIXME
    }

    public static void trip(Coordinate[] locations) {
        StringBuilder request = new StringBuilder("trip?");
        for (Coordinate location : locations) {
            request.append("loc=" + location.latitude + "," + location.longitude + "&");
        }
        sendRequest(request.toString());
    }
    
    private static void sendRequest(String request) {
        StringBuilder responseBody = new StringBuilder();
        try {
            URL url = new URL("http://" + hostname + ":" + port + "/" + request);
            Logger.debug("HTTP", "-----> " + url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches (false);
            connection.setDoInput(true);

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
        } catch (UnknownHostException e) {
            Logger.error("Invalid host: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        } catch (IOException e) {
            Logger.error("Failed to send HTTP request: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
        Logger.debug("HTTP", "<----- " + responseBody.toString());
    }
}
