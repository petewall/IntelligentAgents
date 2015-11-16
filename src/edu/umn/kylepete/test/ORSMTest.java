package edu.umn.kylepete.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.umn.kylepete.Coordinate;
import edu.umn.kylepete.OSRM;

public class ORSMTest {
    
    private Coordinate empireState = new Coordinate(40.748433, -73.985656);

    @Test
    public void testLocate() {
        OSRM.locate(empireState);
    }
}
