package edu.umn.kylepete.env;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;

import edu.umn.kylepete.Logger;

public class Route {
    

    // For Route instructions
	private List<RouteStep> steps;

	private List<Coordinate> viaCoordinates = new ArrayList<Coordinate>();

    // For Route name
	private String name;

    // For Summary
	private String startPoint;
	private String endPoint;
	private int distance;
	private int time;
    
	private int status;

    public static Route fromJsonReader(JsonReader reader) throws IOException {
        Route route = new Route();
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            
            // FOUND ALTERNATIVES (ignored for now)
            if (key.equals("found_alternative")) {
                reader.nextBoolean();
            }
            
            // PERMUTATION (ignored for now)
            else if (key.equals("permutation")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.nextInt();
                }
                reader.endArray();
            }
            
            // ROUTE GEOMETRY (ignored for now)
            else if (key.equals("route_geometry")) {
                reader.nextString();
            }
            
            // ROUTE INSTRUCTIONS
            else if (key.equals("route_instructions")) {
                route.steps = new ArrayList<RouteStep>();
                reader.beginArray();
                while (reader.hasNext()) {
                    route.steps.add(RouteStep.fromJsonReader(reader));
                }
                reader.endArray();
            }
            
            // ROUTE NAME
            else if (key.equals("route_name")) {
                StringBuilder name = new StringBuilder();
                reader.beginArray();
                while (reader.hasNext()) {
                    name.append(reader.nextString() + ", ");
                }
                route.name = name.toString();
                reader.endArray();
            }
            
            // STATUS
            else if (key.equals("status")) {
                route.status = reader.nextInt();
            }
            
            // STATUS MESSAGE (ignored for now)
            else if (key.equals("status_message")) {
                reader.nextString();
            }
            
            // SUMMARY
            else if (key.equals("route_summary")) {
                reader.beginObject();
                String summaryKey;
                while (reader.hasNext()) {
                    summaryKey = reader.nextName();
                    if (summaryKey.equals("start_point")) {
						route.startPoint = reader.nextString();
                    } else if (summaryKey.equals("end_point")) {
						route.endPoint = reader.nextString();
                    } else if (summaryKey.equals("total_distance")) {
                        route.distance = reader.nextInt();
                    } else if (summaryKey.equals("total_time")) {
                        route.time = reader.nextInt();
                    }
                }
                reader.endObject();
            }
            
            // VIA INDICES (ignored for now)
            else if (key.equals("via_indices")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.nextInt();
                }
                reader.endArray();
            }
            
            // VIA POINTS
            else if (key.equals("via_points")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginArray();
					Double lat = reader.nextDouble(); // lat
					Double lon = reader.nextDouble(); // lon
					route.viaCoordinates.add(new Coordinate(lat, lon));
                    reader.endArray();
                }
                reader.endArray();
            }
            
            // HINT DATA (ignored for now)
            else if (key.equals("hint_data")) {
                reader.beginObject();
                String hintKey;
                while (reader.hasNext()) {
                    hintKey = reader.nextName();
                    if (hintKey.equals("checksum")) {
                        reader.nextLong();
                    } else if (hintKey.equals("locations")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.nextString();
                        }
                        reader.endArray();
                    }
                }
                reader.endObject();
            }
            
            else {
				Logger.error("ROUTE", "Unkown key: " + key);
            }
        }
        reader.endObject();
        return route;
    }

	public List<RouteStep> getSteps() {
		return steps;
	}

	public String getName() {
		return name;
	}

	public Coordinate getStartCoordinate() {
		return viaCoordinates.get(0);
	}

	public Coordinate getEndCoordinate() {
		return viaCoordinates.get(viaCoordinates.size() - 1);
	}

	public List<Coordinate> getViaCoordinates() {
		return this.viaCoordinates;
	}

	public int getDistance() {
		return distance;
	}

	public int getTime() {
		return time;
	}

	public int getStatus() {
		return status;
	}
}
