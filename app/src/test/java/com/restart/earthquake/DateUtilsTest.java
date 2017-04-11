package com.restart.earthquake;

import com.restart.earthquake.utilities.DateUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * All comments above tests are adjusted to pacific time zone instead of GMT, but that shouldn't
 * affect their difference.
 */
public class DateUtilsTest {

    @Test
    public void checkDateDifferenceMethodJustNow() throws Exception {
        /* 5/5/2017, 10:29:30 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("Just Now", DateUtils.getDateDifference(1494005370000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceMinutesNotPlural() throws Exception {
        /* 5/5/2017, 10:29:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("1 minute ago", DateUtils.getDateDifference(1494005340000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceMinutesPlural() throws Exception {
        /* 5/4/2017, 10:20:00 PM, 5/4/2017, 10:30:00 PM */
        assertEquals("10 minutes ago", DateUtils.getDateDifference(1493961600000L, 1493962200000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceHoursNotPlural() throws Exception {
        /* 5/5/2017, 9:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("1 hour ago", DateUtils.getDateDifference(1494001800000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceHoursPlural() throws Exception {
        /* 5/5/2017, 1:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("9 hours ago", DateUtils.getDateDifference(1493973000000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceDaysNotPlural() throws Exception {
        /* 5/4/2017, 10:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("1 day ago", DateUtils.getDateDifference(1493919000000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceDaysPlural() throws Exception {
        /* 4/24/2017, 10:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("11 days ago", DateUtils.getDateDifference(1493055000000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceMonthsNotPlural() throws Exception {
        /* 4/5/2017, 10:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("1 month ago", DateUtils.getDateDifference(1491413400000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceMonthsPlural() throws Exception {
        /* 8/9/2016, 10:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("9 months ago", DateUtils.getDateDifference(1470331800000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceYearNotPlural() throws Exception {
        /* 5/5/2016, 10:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("1 year ago", DateUtils.getDateDifference(1462469400000L, 1494005400000L));
    }

    @Test
    public void checkDateDifferenceMethodDifferenceYearPlural() throws Exception {
        /* 8/1/2006, 10:30:00 AM, 5/5/2017, 10:30:00 AM */
        assertEquals("10 years ago", DateUtils.getDateDifference(1154453400000L, 1494005400000L));
    }
}