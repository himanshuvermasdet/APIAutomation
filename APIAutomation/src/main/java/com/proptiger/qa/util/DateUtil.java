package com.proptiger.qa.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: Himanshu.Verma
 */

public class DateUtil {

	public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:MM:ss.000'Z'";

	/**
	 * This function is used to return the date in the format "yyyy-MM-dd'T'HH:MM:ss.000'Z'"
	 * @param days : Number of days which needs to be added or subtracted from the current date.
	 * @return Date in the format defined.
	 */
	public static String currentTimeInUTCPlusDays(int days) {
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() + (days * 100000000));
		return formatter.format(calendar.getTime());
	}

	public static long getUnixTime(int timeToSubtract){
		long unixTime = System.currentTimeMillis() - timeToSubtract;
		return unixTime;
	}
	
	/**
	 * This class returns the current Date
	 * @return
	 */
	public static String getCurrentDate(){

		DateFormat dateFormat = new SimpleDateFormat("dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDateAndTime(){

		DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * This class returns the number of days in a month
	 * @return
	 */
	public static int getNumberOfDaysInMonth(){
		Calendar cal = Calendar.getInstance();
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}
}
