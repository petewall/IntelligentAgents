package edu.umn.kylepete.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.umn.kylepete.Coordinate;
import edu.umn.kylepete.Logger;
import edu.umn.kylepete.Request;

public class MockTaxiData extends TaxiData {
    
    private File file;
    private int requestIndex;
    private List<Request> requests;
    private SimpleDateFormat dateFormat;

    public MockTaxiData() {
        file = new File("/Users/pwall/src/IntelligentAgents/db/1000.csv");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Logger.info("MockTaxiData", "Parsing data file " + file.getName());
        requestIndex = 0;
        readDataFile();
        Logger.info("MockTaxiData", "Parsing done.  Found " + requests.size() + " records");
    }

    private Request lineToRequest(String line) {
        // medallion, hack_license, vendor_id, rate_code, store_and_fwd_flag, pickup_datetime, dropoff_datetime, passenger_count, trip_time_in_secs, trip_distance, pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude
        String[] parts = line.split(",");
        try {
            //long medallion = Long.parseLong(parts[0], 10);
            //long hackLicense = Long.parseLong(parts[1], 10);
            //String vendorId = parts[2];
            //int rateCode = Integer.parseInt(parts[3], 10);
            //store and fwd = parts[4];
            Date pickupDate = dateFormat.parse(parts[5]);
            //String dropoffDate = parts[6];
            int passengers = Integer.parseInt(parts[7], 10);
            //int tripTime = Integer.parseInt(parts[8], 10);
            //double distance = Double.parseDouble(parts[9]);
            double startLongitude = Double.parseDouble(parts[10]);
            double startLatitude = Double.parseDouble(parts[11]);
            double endLongitude = Double.parseDouble(parts[12]);
            double endLatitude = Double.parseDouble(parts[13]);

            return new Request(pickupDate, new Coordinate(startLatitude, startLongitude), new Coordinate(endLatitude, endLongitude), passengers);
        } catch (ParseException e) {
            Logger.error("", "Failed to parse date: " + parts[5]);
            Logger.error(Logger.stackTraceToString(e));
            return null;
        }
    }
    
    private void readDataFile() {
        try {
            requests = new ArrayList<Request>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine(); // Skip the first line
            while ((line = reader.readLine()) != null) {
                requests.add(lineToRequest(line));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            Logger.error("MockTaxiData", "Could not find the data file");
            Logger.error(Logger.stackTraceToString(e));
        } catch (IOException e) {
            Logger.error("MockTaxiData", "Failed to read the data file");
            Logger.error(Logger.stackTraceToString(e));
        }
    }

    @Override
    public long getStartTime() { 
        return requests.get(0).getTime().getTime();
    }
    
    public Request getNextRequest() {
        requestIndex += 50; // FIXME: Get some variety by skipping a few
        if (requestIndex < requests.size()) {
            return requests.get(requestIndex);
        }
        return null;
    }

    @Override
    public List<Request> getRequests(long startTime, long endTime) {
        int startIndex = 0;
        int endIndex = requests.size() - 1;
        for (int i = 0; i < requests.size(); ++i) {
            long requestTime = requests.get(i).getTime().getTime();
            if (requestTime < startTime) {
                startIndex = i;
            }
            
            if (requestTime < endTime) {
                endIndex = i;
            }
        }
        return requests.subList(startIndex, endIndex);
    }
}
