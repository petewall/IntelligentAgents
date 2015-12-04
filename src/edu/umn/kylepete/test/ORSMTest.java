package edu.umn.kylepete.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.env.Coordinate;
import edu.umn.kylepete.env.NamedCoordinate;
import edu.umn.kylepete.env.OSRM;
import edu.umn.kylepete.env.Route;

public class ORSMTest {
    
    private Coordinate empireState = new NamedCoordinate("The Empire State building", 40.748433, -73.985656);
    private Coordinate madisonSquareGarden = new NamedCoordinate("Madison Square Garden", 40.750556, -73.993611);
    private Coordinate theMet = new NamedCoordinate("The Metropolitan Museum of Art", 40.779447, -73.96311);

    @BeforeClass 
    public static void setUpClass() throws IOException {      
        TaxiSystemProperties.loadProperties("taxisystem.properties");
    }

    @Test
    public void testLocate() {
        Coordinate location = OSRM.locate(empireState);
        assertNotNull(location);
        assertTrue(location.distance(empireState) < 100);
    }

    @Test
    public void testNearest() {
        NamedCoordinate location = OSRM.nearest(empireState);
        assertNotNull(location);
        assertEquals("West 33rd Street", location.name);
        assertTrue(location.distance(empireState) < 100);
    }
    
    @Test
    public void testViaRoute() {
        Coordinate[] locations = { empireState, madisonSquareGarden };
        Route route = OSRM.viaRoute(locations);
        assertTrue(route.distance < 1500);
        assertTrue(route.time < 120);
        assertNotNull(route);
    }
    
    @Test
    public void testTable() {
        Coordinate[] locations = { empireState, madisonSquareGarden, theMet };
        List<List<Integer>> table = OSRM.table(locations);
        assertNotNull(table);
        assertEquals(3, table.size());
        for (int i = 0; i < table.size(); i += 1) {
            assertEquals(3, table.get(i).size());
            for (int j = 0; j < table.get(i).size(); j += 1) {
                if (i == j) {
                    assertEquals(0, table.get(i).get(j).intValue());
                }
            }
        }
    }
    
    @Test
    public void testMatch() {}

    @Test
    public void testTrip() {
        Coordinate[] locations = { empireState, madisonSquareGarden, theMet };
        List<Route> routes = OSRM.trip(locations);
        assertNotNull(routes);
    }
}
