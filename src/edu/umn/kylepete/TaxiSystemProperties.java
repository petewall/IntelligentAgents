package edu.umn.kylepete;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TaxiSystemProperties {
	
	private static final String DB_URL = "db.url";
	private static final String DB_USER = "db.user";
	private static final String DB_PASSWORD = "db.password";
	private static final String OSRM_HOST = "osrm.host";
	private static final String OSRM_PORT = "osrm.port";
	private static final String TAXI_COUNT = "taxi.count";
	private static final String REQUEST_COUNT = "request.count";
	private static final String AI_STRATEGY = "ai.strategy";
	
	
	private static String file;
	private static Properties props = null;

	public static void loadProperties(String filePath) throws IOException{
		if(filePath == null){
			throw new IllegalArgumentException("filePath must not be null");
		}
		file = filePath;
		props = new Properties();
		FileInputStream in = new FileInputStream(filePath);
		props.load(in);
		in.close();
	}
	
	public static String getDbUrl(){
		return getRequiredProperty(DB_URL);
	}
	
	public static String getDbUser(){
		return getRequiredProperty(DB_USER);
	}
	
	public static String getDbPassword(){
		return getRequiredProperty(DB_PASSWORD);
	}
	
	public static String getOsrmHost(){
		return getRequiredProperty(OSRM_HOST);
	}
	
	public static int getOsrmPort(){
		String portStr = getRequiredProperty(OSRM_PORT);
		return Integer.parseInt(portStr);
	}
    
    public static int getTaxiCount(){
        String numStr = getRequiredProperty(TAXI_COUNT);
        return Integer.parseInt(numStr);
    }
	
	private static String getRequiredProperty(String key){
		if(props.containsKey(key)){
			return props.getProperty(key);
		}else{
			throw new IllegalStateException("Property file " + file + " does not contain the required property " + key);
		}
	}
}
