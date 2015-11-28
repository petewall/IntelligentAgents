package edu.umn.kylepete.db.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.umn.kylepete.Request;
import edu.umn.kylepete.db.MockTaxiData;

public class MockTaxiDataTest {

    private static final MockTaxiData data = new MockTaxiData();
    
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
