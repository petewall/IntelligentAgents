package edu.umn.kylepete.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.umn.kylepete.env.Coordinate;

public class CoordinateTest {

    @Test
    public void testToString() {
        Coordinate plummer = new Coordinate(44.010278, -92.479722);
        assertEquals("44.010278N 92.479722W", plummer.toString());
        //assertEquals("44°00'37.0\"N 92°28'47.0\"W", plummer.toString());
    }
}
