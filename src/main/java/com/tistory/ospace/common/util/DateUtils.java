package com.tistory.ospace.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
	public static final ZoneId LOCAL = ZoneId.systemDefault();
	public static final ZoneId UTC = ZoneId.of("UTC");
	
	public static final DateTimeFormatterBuilder defaultDateTimeBuilder = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ISO_DATE_TIME);
	
	public static final DateTimeFormatterBuilder defaultDateBuilder = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ISO_DATE)
			.appendOptional(DateTimeFormatter.BASIC_ISO_DATE);
	
	/**
	 * String convert to ZonedDateTime
	 * @param dateStr
	 * @param standardZone
	 * @param changeZone
	 * @return
	 */
	public static ZonedDateTime getDateTime(String dateStr, DateTimeFormatterBuilder builder, ZoneId standardZone, ZoneId changeZone) {
		ZonedDateTime date = parse(dateStr, builder, standardZone).withZoneSameInstant(changeZone);
		return date;
	}
	
	public static ZonedDateTime getDateTime(String dateStr, DateTimeFormatterBuilder builder, ZoneId standardZone) {
		if (StringUtils.isEmpty(dateStr)) return null;
		ZonedDateTime date = parse(dateStr, builder, standardZone);
		return date;
	}
	
	public static ZonedDateTime getDateTime(String dateStr, DateTimeFormatterBuilder builder) {
		if (StringUtils.isEmpty(dateStr)) return null;
		ZonedDateTime date = parse(dateStr, builder, LOCAL);
		return date;
	}
	
	/**
	 * Builder를 넘겨주지 않는 경우 default로 선언 해놓은 DateTimeFormatterBuilder 사용
	 * @param dateStr
	 * @return
	 */
	public static ZonedDateTime getDateTime(String dateStr) {
		if (StringUtils.isEmpty(dateStr)) return null;
		ZonedDateTime date = parse(dateStr, LOCAL);
		return date;
	}
	
	/**
	 * Calendar convert to ZonedDateTime
	 * @param calendar
	 * @return
	 */
	public static ZonedDateTime getDateTime(Calendar calendar, ZoneId standardZone, ZoneId changeZone) {
		ZonedDateTime date = ZonedDateTime.ofInstant(calendar.toInstant(), standardZone).withZoneSameInstant(changeZone);
		return date;
	}
	
	public static ZonedDateTime getDateTime(Calendar calendar, ZoneId standardZone) {
		ZonedDateTime date = ZonedDateTime.ofInstant(calendar.toInstant(), standardZone);
		return date;
	}
	
	public static ZonedDateTime getDateTime(Calendar calendar) {
		ZonedDateTime date = ZonedDateTime.ofInstant(calendar.toInstant(), LOCAL);
		return date;
	}
	
	/**
	 * Millisecond convert to ZonedDateTime
	 * @param tms
	 * @return
	 */
	public static ZonedDateTime getDateTimeOfMilli(long tms) {
		return Instant.ofEpochMilli(tms).atZone(LOCAL);
	}
	
	/**
	 * parse ZonedDateTime
	 * @param dateStr
	 * @param zone
	 * @return
	 */
	private static ZonedDateTime parse(String dateStr, DateTimeFormatterBuilder builder, ZoneId zone) {
		if(dateStr.length() > 10) {
			return ZonedDateTime.parse(dateStr, builder.toFormatter().withZone(zone));
		} else {
			LocalDate localDate = LocalDate.parse(dateStr, builder.toFormatter());
			return ZonedDateTime.of(localDate, LocalTime.MIDNIGHT, zone);
		}
	}
	
	private static ZonedDateTime parse(String dateStr, ZoneId zone) {
		if(dateStr.length() > 10) {
			return ZonedDateTime.parse(dateStr, defaultDateTimeBuilder.toFormatter().withZone(zone));
		} else {
			LocalDate localDate = LocalDate.parse(dateStr, defaultDateBuilder.toFormatter());
			return ZonedDateTime.of(localDate, LocalTime.MIDNIGHT, zone);
		}
	}
	
	/**
	 * change date format
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String datePattern(ZonedDateTime date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return date.format(formatter);
	}
	
	/**
	 * 요일명 가져오기
	 * locale parameter 넘겨줄 시 해당 locale의 요일명으로 반환
	 * @param date
	 * @param locale
	 * @return
	 */
	public static String getDayOfWeek(ZonedDateTime date, Locale locale) {
		return date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
	}
	public static String getDayOfWeek(ZonedDateTime date) {
		return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
	}
	
	/**
	 * get today
	 * @return
	 */
	public static ZonedDateTime getToday() {
		return ZonedDateTime.now(ZoneId.systemDefault());
	}
	
	public static String getToday(String pattern) {
		return datePattern(getToday(), pattern);
	}
	
	//current 날짜 기준으로 생일을 적용하여 나이를 계산
	public static int getAge(ZonedDateTime birthday, ZonedDateTime current) {
		return (int) ChronoUnit.YEARS.between(birthday, current);
	}
	
	/**
	 * get difference of days
	 * @param start
	 * @param end
	 * @return
	 */
	public static long getDiffOfDays(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());
	}
	
	public static long getDiffOfDays(String start, String end) {
		return ChronoUnit.DAYS.between(getDateTime(start).toInstant(), getDateTime(end).toInstant());
	}
	
	/**
	 * get difference of minutes
	 * @param start
	 * @param end
	 * @return
	 */
	public static long getDiffOfMinutes(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.MINUTES.between(start.toInstant(), end.toInstant());
	}
	
	public static long getDiffOfMinutes(String start, String end) {
		return ChronoUnit.MINUTES.between(getDateTime(start), getDateTime(end));
	}
	
	/**
	 * get difference of seconds
	 * @param start
	 * @param end
	 * @return
	 */
	public static long getDiffOfSeconds(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.SECONDS.between(start.toInstant(), end.toInstant());
	}
	
	public static long getDiffOfSeconds(String start, String end) {
		return ChronoUnit.SECONDS.between(getDateTime(start).toInstant(), getDateTime(end).toInstant());
	}
	
	/**
	 * 입력받은 시간(분) 정보로 ZoneId 반환
	 * @param minutes
	 * @return
	 */
	public static ZoneId getZoneIdOfMinutes(long minutes) {
		int hours = (int)(minutes / 60);
		return ZoneOffset.ofHours(hours);
	}
}
