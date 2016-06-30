/*
 * Pprun's Public Domain.
 */
package com.letv.common.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Daily used Calendar utility methods.
 * 
 * @author <a href="mailto:quest.run@gmail.com">pprun</a>
 */
public final class CalendarUtil {

	private static final Logger log = LoggerFactory.getLogger(CalendarUtil.class);
	public static final String CREDIT_CARD_DATE_FORMAT = "MM/yyyy";
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SHORT_DATE_DOT_FORMAT = "yyyy.MM.dd";
	public static final String SHORT_DATE_FORMAT_NO_DASH = "yyyyMMdd";
	public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SIMPLE_DATE_FORMAT_NO_DASH = "yyyyMMddHHmmss";
	public static final String LOG_DATE_FORMAT = "yyyyMMdd_HH00";
	public static final String ZONE_DATE_FORMAT = "EEE yyyy-MM-dd HH:mm:ss zzz";
	public static final String DATE_FORMAT = "yyyy/MM/dd EEE";
	public static final String TIME_FORMAT = "HH:mm";

	public static int daysBetween(Calendar startTime, Calendar endTime) {
		if (startTime == null) {
			throw new IllegalArgumentException("startTime is null");
		}
		if (endTime == null) {
			throw new IllegalArgumentException("endTime is null");
		}
		if (startTime.compareTo(endTime) > 0) {
			throw new IllegalArgumentException("endTime is before the startTime");
		}
		return (int) ((endTime.getTimeInMillis() - startTime.getTimeInMillis()) / (1000 * 60 * 60 * 24));
	}

	public static Calendar startOfDayTomorrow() {
		Calendar calendar = Calendar.getInstance();
		truncateDay(calendar);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar;
	}

	/**
	 * create a calendar for start of day yesterday.
	 * 
	 * @return
	 */
	public static Calendar startOfDayYesterday() {
		Calendar yesterday = Calendar.getInstance();
		truncateDay(yesterday);
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		return yesterday;
	}

	/**
	 * Truncate the calendar's Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND,
	 * Calendar.MILLISECOND to ZERO.
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar truncateDay(Calendar calendar) {
		if (calendar == null) {
			throw new IllegalArgumentException("input is null");
		}
		calendar.set(Calendar.AM_PM, Calendar.AM);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * format a calendar using {@link SimpleDateFormat}, with default pattern.
	 * if a null calendar is passed in, empty string is returned.
	 * 
	 * @param calendar
	 * @return
	 */
	public static String format(Calendar calendar) {
		String formatted = "";
		if (calendar != null) {
			formatted = new SimpleDateFormat().format(calendar.getTime());
		}
		return formatted;
	}

	/**
	 * format a Time using {@link SimpleDateFormat}, with default pattern.
	 * if a null calendar is passed in, empty string is returned.
	 * 
	 * @param time
	 * @return
	 */
	public static String format(Time time) {
		String formatted = "";
		if (time != null) {
			formatted = new SimpleDateFormat(TIME_FORMAT).format(time.getTime());
		}
		return formatted;
	}

	/**
	 * Return the String representation of the Calendar against the given
	 * format.
	 * 
	 * @param date
	 *            the date to format, such as 'yyyy-MM-dd HH:mm:ss' for long
	 *            date format with 24H
	 * @param format
	 *            the date format pattern
	 * @return the format Date String.
	 */
	public static String getDateString(Calendar calendar, String format) {
		if (calendar == null) {
			return null;
		}
		return getDateString(calendar.getTime(), format);
	}

	/**
	 * Return the String representation of the Date against the given format.
	 * 
	 * @param date
	 *            the date to format
	 * @param format
	 *            the date format pattern
	 * @return the format Date String.
	 */
	public static String getDateString(Date date, String format) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * Return the String representation of the Date against the given format.
	 * 
	 * @param date
	 *            the date to format
	 * @param format
	 *            the date format pattern
	 * @return the format Date String.
	 */
	public static String getDateString(Date date, String format, Locale locale) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
		return sdf.format(date);
	}

	/**
	 * Return the String representation of the Calendar for the given format.
	 * 
	 * @param cal
	 *            the Calendar to format
	 * @param format
	 *            the date format pattern, e.g.,
	 *            {@code "EEE yyyy-MM-dd HH:mm:ss zzz"}
	 * @param timeZone
	 *            the timeZone for formatter, e.g.,
	 *            {@code TimeZone.getTimeZone("UTC")}
	 * @param locale
	 *            the timeZone for formatter, e.g., {@code Locale.CHINA}, should
	 *            be original from user profile
	 * 
	 * @return the format Date String.
	 */
	public static String getDateStringWithZone(Calendar cal, String format, TimeZone timeZone, Locale locale) {
		if (cal == null) {
			return null;
		}
		if (format == null) {
			format = ZONE_DATE_FORMAT;
		}
		if (timeZone == null) {
			TimeZone.getTimeZone(CommonUtil.TIME_ZONE_UTC);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);

		sdf.setTimeZone(timeZone);
		return sdf.format(cal.getTime());
	}

	/**
	 * Parses the <tt>Date</tt> from the given date string and the format
	 * pattern.
	 * 
	 * @param dateString
	 * @param pattern
	 *            the date format
	 * @throws {@link IllegalArgumentException} if date format error
	 * @return
	 */
	public static Date parseDate(String dateString, String pattern) {
		Date date = null;
		try {
			DateFormat format = new SimpleDateFormat(pattern);
			date = format.parse(dateString);
		} catch (ParseException ex) {
			log.error("Invalid date string: " + dateString, ex);
			throw new IllegalArgumentException("Invalid date string: " + dateString, ex);
		}

		return date;
	}

	public static Calendar parseCalendarShort(String dateString) {
		if (!StringUtils.hasText(dateString)) {
			return null;
		}
		return parseCalendar(dateString, SHORT_DATE_FORMAT);
	}

	public static Calendar parseCalendar(String dateString) {
		if (!StringUtils.hasText(dateString)) {
			return null;
		}
		return parseCalendar(dateString, SIMPLE_DATE_FORMAT);
	}

	/**
	 * Parses the <tt>Date</tt> from the given date string and the format
	 * pattern and return it as a {@link Calendar} instance.
	 * 
	 * @param dateString
	 * @param pattern
	 *            the date format
	 * @throws {@link IllegalArgumentException} if date format error
	 * @return
	 */
	public static Calendar parseCalendar(String dateString, String pattern) {
		Date date = parseDate(dateString, pattern);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	/**
	 * Parses the <tt>Date</tt> from the given date string with the format
	 * pattern 'yyyy-MM-dd'.
	 * 
	 * @param dateString
	 * @throws {@link IllegalArgumentException} if date format error
	 * @return
	 */
	public static Date parseShortDate(String dateString) {
		if (!StringUtils.hasText(dateString)) {
			return null;
		}
		Date date = null;
		try {
			DateFormat format = new SimpleDateFormat(SHORT_DATE_FORMAT);
			date = format.parse(dateString);
		} catch (ParseException ex) {
			log.error("Invalid date string: " + dateString, ex);
			throw new IllegalArgumentException("Invalid date string: " + dateString, ex);
		}

		return date;
	}

	/**
	 * return one day before the given date.
	 * 
	 * @param date
	 *            the given date
	 * @return the adjusted date.
	 */
	public static Calendar backOneDay(Calendar date) {
		Calendar cal = (Calendar) date.clone();
		cal.add(Calendar.DATE, -1);
		return cal;
	}

	/**
	 * Get how many days in current month.
	 * 
	 */
	public static int daysForCurrentMonth() {
		Calendar c = Calendar.getInstance();
		int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		return days;
	}

	/**
	 * Return the Calendar for the give date.
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar fromDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c;
	}

	/**
	 * return the EPOCH = "1970-01-01 00:00:00"
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static Calendar epoch() {

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(0);
		return c;
	}

	public static long getSimpleDateTimeMillis(long timeMillis) {
		Date date = new Date(timeMillis);
		String dateStr = getDateString(date, SHORT_DATE_FORMAT);
		Date transformDate = parseDate(dateStr, SHORT_DATE_FORMAT);
		return transformDate.getTime();
	}

	/**
	 * get the date from a day with days
	 * 
	 * @author <a href="mailto:wang-shuai@letv.com">Ousui</a>
	 * @param from
	 *            which day
	 * @param days
	 *            interval days, 0: today; positive: future; negative: history.
	 * @return
	 */
	public static Calendar getDateFromDate(Date from, long days) {
		long froml = from.getTime();
		// 时间间隔。
		long interval = days * 24l * 60l * 60l * 1000l;
		long millis = froml + interval;
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(millis);
		return now;
	}

	/**
	 * get the date from a day with days
	 * 
	 * @author <a href="mailto:wang-shuai@letv.com">Ousui</a>
	 * @param from
	 *            which day
	 * @param days
	 *            interval days, 0: today; positive: future; negative: history.
	 * @return String
	 * 
	 */
	public static String getDateFromDate(Date from, long days, String format) {
		Calendar c = CalendarUtil.getDateFromDate(from, days);
		return CalendarUtil.getDateString(c, format);
	}

	public static String getDateFromDate(String from, long days, String format) {
		Date d = CalendarUtil.parseDate(from, format);
		Calendar c = CalendarUtil.getDateFromDate(d, days);
		return CalendarUtil.getDateString(c, format);
	}
	
	/**
	 * 获得当前日期属于星期几
	 * @param date
	 * @return {星期一 ：1, 星期二： 2 ... 星期日： 7}
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if(day == 0)
			return 7;
		return day;
	}

        public static Calendar getDayStart() {
            Calendar cal = Calendar.getInstance();
            cal = truncateDay(cal);
            return cal;
        }
        
        public static Calendar getWeekDayStart() {
            Calendar cal = Calendar.getInstance();
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
            cal.add(Calendar.DATE, -day_of_week);
            cal = truncateDay(cal);
            return cal;
        }
        
        public static Calendar getMonthDayStart() {
            Calendar cal = Calendar.getInstance();
            int day_of_month = cal.get(Calendar.DAY_OF_MONTH) - 1;
            cal.add(Calendar.DATE, - day_of_month);
            cal = truncateDay(cal);
            return cal;
        }
        
        public static void main(String[] args) {
			System.out.println(getDayOfWeek(new Date()));
		}
        
}
