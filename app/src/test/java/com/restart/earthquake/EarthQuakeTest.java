package com.restart.earthquake;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test methods of EarthQuake
 */
public class EarthQuakeTest {

    @Test
    public void checkTwoEarthObjectAreSame() throws Exception {
        EarthQuake first = new EarthQuake("1", "2", "3", 4, 5, "6", 7, 7);
        EarthQuake second = new EarthQuake("1", "2", "3", 4, 5, "6", 7, 7);
        assertTrue(first.equals(second));
    }
}
