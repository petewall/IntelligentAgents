package edu.umn.kylepete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;

/**
 * Send requests to an OSRM server
 * API from https://github.com/Project-OSRM/osrm-backend/wiki/Server-api
 * @author pwall
 *
 */
public class OSRM {
    private static String hostname = "192.168.0.100";
    private static int port = 5000;
    
    public static Coordinate locate(Coordinate location) {
        String response = sendRequest("locate?loc=" + location.latitude + "," + location.longitude);
        
        int status = 1;
        Coordinate mappedLocation = null;
        JsonReader reader = new JsonReader(new StringReader(response));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("status")) {
                    status = reader.nextInt();
                } else if (key.equals("mapped_coordinate")) {
                    reader.beginArray();
                    mappedLocation = new Coordinate(reader.nextDouble(), reader.nextDouble());
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            Logger.error("OSRM", "Failed to parse the \"locate\" response: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
        
        if (status != 0) {
            Logger.error("OSRM", "Non-zero status for locate: " + status + "  Location was " + location);
        }
        return mappedLocation;
    }

    public static NamedCoordinate nearest(Coordinate location) {
        String response = sendRequest("nearest?loc=" + location.latitude + "," + location.longitude);
        
        int status = 1;
        String name = null;
        Coordinate mappedLocation = null;
        JsonReader reader = new JsonReader(new StringReader(response));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("status")) {
                    status = reader.nextInt();
                } else if (key.equals("name")) {
                    name = reader.nextString();
                } else if (key.equals("mapped_coordinate")) {
                    reader.beginArray();
                    mappedLocation = new Coordinate(reader.nextDouble(), reader.nextDouble());
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            Logger.error("OSRM", "Failed to parse the \"nearest\" response: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
        
        if (status != 0) {
            Logger.error("OSRM", "Non-zero status for nearest: " + status + "  Location was " + location);
        }
        return new NamedCoordinate(name, mappedLocation);
    }
    
    public static Route viaRoute(Coordinate[] locations) {
        StringBuilder request = new StringBuilder("viaroute?alt=false&");
        for (Coordinate location : locations) {
            request.append("loc=" + location.latitude + "," + location.longitude + "&");
        }
        request.deleteCharAt(request.length() - 1);
        String response = sendRequest(request.toString());
        
        int status = 1;
        Route route = null;
        JsonReader reader = new JsonReader(new StringReader(response));
        try {
            route = Route.fromJsonReader(reader);
            reader.close();
        } catch (IOException e) {
            Logger.error("OSRM", "Failed to parse the \"viaroute\" response: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }

        if (route.status != 0) {
            Logger.error("OSRM", "Non-zero status for viaroute: " + status + "  Locations were " + locations);
        }
        
        return route;
    }
    
    public static List<List<Integer>> table(Coordinate[] locations) {
        StringBuilder request = new StringBuilder("table?");
        for (Coordinate location : locations) {
            request.append("loc=" + location.latitude + "," + location.longitude + "&");
        }
        request.deleteCharAt(request.length() - 1);
        String response = sendRequest(request.toString());
        
        List<List<Integer>> distanceTable = new ArrayList<List<Integer>>();
        JsonReader reader = new JsonReader(new StringReader(response));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("distance_table")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        List<Integer> distances = new ArrayList<Integer>();
                        reader.beginArray();
                        while (reader.hasNext()) {
                            distances.add(new Integer(reader.nextInt()));
                        }
                        reader.endArray();
                        distanceTable.add(distances);
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            Logger.error("OSRM", "Failed to parse the \"table\" response: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
        return distanceTable;
    }

    public static void match() {
        // FIXME
    }

    public static List<Route> trip(Coordinate[] locations) {
        StringBuilder request = new StringBuilder("trip?");
        for (Coordinate location : locations) {
            request.append("loc=" + location.latitude + "," + location.longitude + "&");
        }
        request.deleteCharAt(request.length() - 1);
        String response = sendRequest(request.toString());
        
        List<Route> trips = new ArrayList<Route>();
        JsonReader reader = new JsonReader(new StringReader(response));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("trips")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        trips.add(Route.fromJsonReader(reader));
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            Logger.error("OSRM", "Failed to parse the \"table\" response: " + e.getMessage());
            Logger.error(Logger.stackTraceToString(e));
        }
        return trips;
    }
    
    private static String sendRequest(String request) {
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
        return responseBody.toString();
    }
}
