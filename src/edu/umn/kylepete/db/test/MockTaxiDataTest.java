package edu.umn.kylepete.db.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.db.MockTaxiData;
import edu.umn.kylepete.env.OSRM;
import edu.umn.kylepete.env.Request;

public class MockTaxiDataTest {

	private static MockTaxiData data;
    
	@BeforeClass
	public static void setUpClass() throws IOException {
		TaxiSystemProperties properties = new TaxiSystemProperties("taxisystem.properties");
		OSRM.hostname = properties.getOsrmHost();
		OSRM.port = properties.getOsrmPort();
		data = new MockTaxiData(15);
	}

    @Test
    public void testGetStartTime() {
        assertTrue(data.getStartTime() > 0);
    }

    @Test
    public void testGetRequests() {
        List<Request> requests =  data.getRequests(data.getStartTime(), data.getStartTime() + 10);
        assertTrue(requests.size() > 0);
    }
}
