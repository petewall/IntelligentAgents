package edu.umn.kylepete.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.Request;

public class PostgreSQLTaxiData extends TaxiData {

	private static final int DEFAULT_PAGE_SIZE = 1000;

	private Connection conn;
	private int pageSize;
	private int nthRow;
	private int limit;
	private Timestamp prevPickupTime;
	private long prevId;

	private List<Request> curPageRequests;
	private int curRequestInPage;

	public PostgreSQLTaxiData(TaxiSystemProperties props) throws ParseException {
		this(props, DEFAULT_PAGE_SIZE);
	}

	public PostgreSQLTaxiData(TaxiSystemProperties props, int pageSize) throws ParseException {
		this(props.getDbUrl(), props.getDbUser(), props.getDbPassword(), new Timestamp(props.getTimeStart().getTime()), props.getRequestPercentage(), pageSize);
	}
    
	public PostgreSQLTaxiData(String url, String user, String password, Timestamp startTime, int percentOfRequests) {
		this(url, user, password, startTime, percentOfRequests, DEFAULT_PAGE_SIZE);
	}

	public PostgreSQLTaxiData(String url, String user, String password, Timestamp startTime, int percentOfRequests, int pageSize) {
        try {
            Properties props = new Properties();
			props.setProperty("user", user);
			props.setProperty("password", password);
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
			Logger.error("DATABASE", "Failed to open connection: " + e.getMessage());
			Logger.error("DATABASE", Logger.stackTraceToString(e));
        }
		this.pageSize = pageSize;
		this.prevPickupTime = startTime;
		this.prevId = 0;
		this.nthRow = 100 / percentOfRequests;
		this.limit = nthRow * pageSize;
		this.curRequestInPage = pageSize;
    }

	private PreparedStatement getSqlStatement(Timestamp prevPickupTime, long prevId) throws SQLException {
		String query = "SELECT * FROM (" +
				"  SELECT pickup_datetime, passenger_count, " +
				"    pickup_latitude, pickup_longitude, " +
				"    dropoff_latitude, dropoff_longitude, " +
				"    id, row_number() over(order by pickup_datetime, id) " +
				"  FROM trip " +
				"  WHERE (pickup_datetime, id) > (?, ?)" +
				"  ORDER BY pickup_datetime, id " +
				"  LIMIT ?) AS t " +
				"WHERE row_number % ? = 0";

		PreparedStatement statement = conn.prepareStatement(query);
		statement.setTimestamp(1, prevPickupTime);
		statement.setLong(2, prevId);
		statement.setInt(3, this.limit);
		statement.setInt(4, this.nthRow);
		return statement;
	}

	private void getNextPage() {
		this.curRequestInPage = 0;
		this.curPageRequests = new ArrayList<Request>(pageSize);
    	try {
			PreparedStatement statement = getSqlStatement(prevPickupTime, prevId);
			ResultSet result = statement.executeQuery();
			while(result.next()){
				Timestamp pickupTime = result.getTimestamp(1);
				int passengers = result.getInt(2);
				Coordinate pickup = new Coordinate(result.getDouble(3), result.getDouble(4));
				Coordinate dropoff = new Coordinate(result.getDouble(5), result.getDouble(6));
				Request request = new Request(pickupTime, pickup, dropoff, passengers);
				this.curPageRequests.add(request);
				this.prevId = result.getLong(7);
				this.prevPickupTime = pickupTime;
			}
            statement.close();
		} catch (SQLException e) {
			Logger.error("DATABASE", "SQL execption: " + e.getMessage());
			Logger.error("DATABASE", Logger.stackTraceToString(e));
		}
    	
    }
    
    public Request getNextRequest() {
        this.curRequestInPage++;
		if (this.curRequestInPage >= pageSize) {
        	getNextPage();
        }
		if (this.curRequestInPage >= this.curPageRequests.size()) {
			return null;
		}
		return this.curPageRequests.get(this.curRequestInPage);
    }
}
