package com.example.cronogramacapilar;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TreatmentTest {
    @Test
    public void calculateNextDate_UnitIsDay_returnsCorrectDate() {
        int day = 11;
        int year = 2014;
        int numberOfDays = 3;
        int month = Calendar.FEBRUARY;
        Date lastDate = new GregorianCalendar(year, month, day).getTime();

        Date nextDate = Treatment.calculateNextDate(lastDate, numberOfDays, "dias");

        Calendar cal = Calendar.getInstance();
        cal.setTime(nextDate);
        assertEquals(day + numberOfDays, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH));
    }
}