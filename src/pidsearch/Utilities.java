/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Calendar;

/**
 *
 * @author jethro
 */
public class Utilities {

    public static String strTime(int time) {
        return String.format("%d.%02d", time / 60, time % 60);
    }
    public static String strTime(Calendar cal){
        return String.format("%d.%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
    
    public static String strDate(Calendar cal){
        return String.format("%d.%d.%d", cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR));
    }

    public static Calendar parseDate(String str) {
        return parseDate(str, Calendar.getInstance());
    }

    public static Calendar parseDate(String str, Calendar cal) {
        String[] parts;
        parts = str.split("\\.");
        if (parts.length > 3) {
            System.out.println("Wrong date " + str);
            return null;
        }
        int day = 0;
        int month = 0;
        int year = 0;
        if (parts.length > 0) {
            try {
                day = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
            }
        }
        if (parts.length > 1) {
            try {
                month = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
            }
        }
        if (parts.length > 2) {
            try {
                year = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
            }
        }
        if (day != 0) {
            cal.set(Calendar.DAY_OF_MONTH, day);
        }
        if (month != 0) {
            cal.set(Calendar.MONTH, month);
        }
        if (year != 0) {
            cal.set(Calendar.YEAR, year);
        }
        return cal;
    }

    public static Calendar parseTime(String str) {
        return parseTime(str, Calendar.getInstance());
    }

    public static Calendar parseTime(String str, Calendar cal) {
        String[] parts;
        parts = str.split("\\.");
        if (parts.length != 2) {
            System.out.println("Wrong time seg "+ parts.length+"," + str);
            return null;
        }
        int hour = 0;
        int min = 0;
        try {
            hour = Integer.parseInt(parts[0]);
            min = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong time " + str);
            return null;
        }
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, min);
        return cal;
    }
}
