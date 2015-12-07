package edu.umn.kylepete;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class TaxiSystemProperties {
	
	private static final String DB_URL = "db.url";
	private static final String DB_USER = "db.user";
	private static final String DB_PASSWORD = "db.password";
	private static final String OSRM_HOST = "osrm.host";
	private static final String OSRM_PORT = "osrm.port";
	private static final String TAXI_COUNT = "taxi.count";
	private static final String AI_STRATEGY = "ai.strategy";
	private static final String RANDOM_SEED = "random.seed";
	private static final String TIME_START = "time.start";
	private static final String REQUEST_MAX_TIME = "request.max.time";
	private static final String REQUEST_PERCENT = "request.percentage";
	private static final String LOGGER_DEBUG = "logger.debug";
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String file;
	private static Properties props = null;

	public TaxiSystemProperties(String filePath) throws IOException {
		if(filePath == null){
			throw new IllegalArgumentException("filePath must not be null");
		}
		Logger.info("TAXI SYSTEM", "Loading properties from " + filePath);
		file = filePath;
		props = new Properties();
		FileInputStream in = new FileInputStream(filePath);
		props.load(in);
		in.close();
	}
	
	public String getDbUrl() {
		return getRequiredProperty(DB_URL);
	}
	
	public String getDbUser() {
		return getRequiredProperty(DB_USER);
	}
	
	public String getDbPassword() {
		return getRequiredProperty(DB_PASSWORD);
	}
	
	public String getOsrmHost() {
		return getRequiredProperty(OSRM_HOST);
	}
	
	public int getOsrmPort() {
		String portStr = getRequiredProperty(OSRM_PORT);
		return Integer.parseInt(portStr);
	}
    
	public int getTaxiCount() {
        String numStr = getRequiredProperty(TAXI_COUNT);
        return Integer.parseInt(numStr);
    }
	
	public Long getRandomSeed() {
		String value = props.getProperty(RANDOM_SEED);
		if(value == null || value.isEmpty()){
			return null;
		}else{
			return Long.parseLong(value);
		}
	}
	
	public Date getTimeStart() throws ParseException {
		String value = getRequiredProperty(TIME_START);
		return dateFormat.parse(value);
	}

	public Date getRequestMaxTime() throws ParseException {
		String value = getRequiredProperty(REQUEST_MAX_TIME);
		return dateFormat.parse(value);
	}

	public int getRequestPercentage() {
		String value = getRequiredProperty(REQUEST_PERCENT);
		int percent = Integer.parseInt(value);
		if (percent < 1 || percent > 100) {
			throw new IllegalArgumentException(REQUEST_PERCENT + " property must be an integer between 1 and 100. Found value " + percent);
		}
		return percent;
	}

	public String getAIStrategy() {
		return getRequiredProperty(AI_STRATEGY);
	}

	public boolean getLoggerDebug() {
		String value = getRequiredProperty(LOGGER_DEBUG);
		return Boolean.valueOf(value);
	}

	private String getRequiredProperty(String key) {
		String value = props.getProperty(key);
		if(value != null && !value.isEmpty()){
			return value;
		}else{
			throw new IllegalStateException("Property file " + file + " does not contain the required property " + key);
		}
	}
}
