package cis573.carecoor.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

public class Utils {
	
	private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("M/d/yyyy", Locale.US);
	private static final SimpleDateFormat mTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);
	private static final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("M/d/yyyy h:mm a", Locale.US);

	public static void callPhone(Context context, String number) {
		
	}
	
	public static String getWeekNameShort(int week) {
		switch(week) {
		case Calendar.SUNDAY: return "SUN";
		case Calendar.MONDAY: return "MON";
		case Calendar.TUESDAY: return "TUE";
		case Calendar.WEDNESDAY: return "WED";
		case Calendar.THURSDAY: return "THU";
		case Calendar.FRIDAY: return "FRI";
		case Calendar.SATURDAY: return "SAT";
		default: return "";
		}
	}
	
	public static String getWeekName(int week) {
		switch(week) {
		case Calendar.SUNDAY: return "Sunday";
		case Calendar.MONDAY: return "Monday";
		case Calendar.TUESDAY: return "Tuesday";
		case Calendar.WEDNESDAY: return "Wednesday";
		case Calendar.THURSDAY: return "Thursday";
		case Calendar.FRIDAY: return "Friday";
		case Calendar.SATURDAY: return "Saturday";
		default: return "";
		}
	}
	
	public static String get12ClockTime(int hour) {
		String ampm = hour >= 12 ? "PM" : "AM";
		int hour12 = hour > 12 ? hour - 12 : hour;
		return "" + hour12 + ":00 " + ampm;
	}
	
	public static boolean inSameDay(Date day1, Date day2) {
		Calendar cal1 = Calendar.getInstance(Locale.US);
		Calendar cal2 = Calendar.getInstance(Locale.US);
		cal1.setTime(day1);
		cal2.setTime(day2);
		return inSameDay(cal1, cal2);
	}
	
	public static boolean inSameDay(Calendar day1, Calendar day2) {
		return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR)
				&& day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
				&& day1.get(Calendar.DATE) == day2.get(Calendar.DATE);
	}
	
	public static String getDateString(Date date) {
		return mDateFormat.format(date);
	}
	
	public static String getTimeString(Date date) {
		return mTimeFormat.format(date);
	}
	
	public static String getDateTimeString(Date date) {
		return mDateTimeFormat.format(date);
	}
}
