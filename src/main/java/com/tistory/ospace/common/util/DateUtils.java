package com.tistory.ospace.common.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DateUtils {
	public static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
	public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
	
	public static final DateTimeFormatterBuilder defaultDateTimeBuilder = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ISO_DATE_TIME);
	
	public static final DateTimeFormatterBuilder defaultDateBuilder = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ISO_DATE)
			.appendOptional(DateTimeFormatter.BASIC_ISO_DATE);
	
	/**
	 * String convert to ZonedDateTime
	 * 
	 * @param dateStr
	 * @param standardZone
	 * @param changeZone
	 * @return
	 */
	public static ZonedDateTime toZonedDateTime(String dateStr, DateTimeFormatterBuilder builder, ZoneId standardZone, ZoneId changeZone) {
		ZonedDateTime date = toZonedDateTime(dateStr, builder, standardZone).withZoneSameInstant(changeZone);
		return date;
	}
	
	public static ZonedDateTime toZonedDateTime(String dateStr, DateTimeFormatterBuilder builder) {
		if (StringUtils.isEmpty(dateStr)) return null;
		ZonedDateTime date = toZonedDateTime(dateStr, builder, DEFAULT_ZONE);
		return date;
	}
	
    public static ZonedDateTime toZonedDateTime(String date, DateTimeFormatterBuilder builder, ZoneId zone) {
        if (StringUtils.isEmpty(date) || null == builder || null == zone) return null;
        return toZonedDateTime(date, builder.toFormatter().withZone(zone));
    }
	
    public static ZonedDateTime toZonedDateTime(String date, String pattern, ZoneId zone) {
        if(StringUtils.isEmpty(date) || StringUtils.isEmpty(pattern)) return null;
        return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(pattern).withZone(zone));
    }
    
	public static ZonedDateTime toZonedDateTime(String date, String pattern) {
        if(StringUtils.isEmpty(date) || StringUtils.isEmpty(pattern)) return null;
        return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }
	
	public static ZonedDateTime toZonedDateTime(String date, DateTimeFormatter formatter) {
	    if(StringUtils.isEmpty(date) || null == formatter) return null;
	    return ZonedDateTime.parse(date, formatter);
	}
	
	/**
	 * Calendar convert to ZonedDateTime
	 * 
	 * @param calendar
	 * @return
	 */
	public static ZonedDateTime toZonedDateTime(Calendar calendar, ZoneId standardZone, ZoneId changeZone) {
	    if(null == calendar || null == standardZone || null == changeZone) return null;
		ZonedDateTime date = ZonedDateTime.ofInstant(calendar.toInstant(), standardZone).withZoneSameInstant(changeZone);
		return date;
	}
	
	public static ZonedDateTime toZonedDateTime(Calendar calendar, ZoneId standardZone) {
	    if(null == calendar || null == standardZone) return null;
		ZonedDateTime date = ZonedDateTime.ofInstant(calendar.toInstant(), standardZone);
		return date;
	}
	
	public static ZonedDateTime toZonedDateTime(Calendar calendar) {
	    if(null == calendar) return null;
		ZonedDateTime date = ZonedDateTime.ofInstant(calendar.toInstant(), DEFAULT_ZONE);
		return date;
	}
	
	/**
	 * Millisecond convert to ZonedDateTime
	 * 
	 * @param tms
	 * @return
	 */
	public static ZonedDateTime toZonedDateTime(long tms) {
		return Instant.ofEpochMilli(tms).atZone(DEFAULT_ZONE);
	}
	
    /**
	 * ZonedDateTime convert to String with pattern
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(ZonedDateTime date, String pattern) {
		if(null == date || null == pattern) return null;
		return toString(date, DateTimeFormatter.ofPattern(pattern));
	}
	
	public static String toString(ZonedDateTime date, DateTimeFormatter formatter) {
	    if(null == date || null == formatter) return null;
	    return date.format(formatter);
	}
	
	/**
	 * LocalTime convert to String
	 * 
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String toString(LocalTime time, String pattern) {
        if(null == time || null == pattern) return null;
        return toString(time, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static String toString(LocalTime time, DateTimeFormatter formatter) {
        if(null == time || null == formatter) return null;
        return time.format(formatter);
    }
    
    public static LocalTime toLocalTime(String time, String pattern) {
        if(StringUtils.isEmpty(time) || StringUtils.isEmpty(pattern)) return null;
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalTime toLocalTime(String time, DateTimeFormatter formatter) {
        if(StringUtils.isEmpty(time) || null == formatter) return null;
        return LocalTime.parse(time, formatter);
    }
    
    /**
     * LocalDateTime convert to String
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String toString(LocalDateTime date, String pattern) {
        if(null == date || null == pattern) return null;
        return toString(date, DateTimeFormatter.ofPattern(pattern));
    }

    public static String toString(LocalDateTime date, DateTimeFormatter formatter) {
        if(null == date || null == formatter) return null;
        return date.format(formatter);
    }
	
    /**
     * String convert to LocalDateTime with pattern
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static LocalDateTime toLocalDateTime(String date, String pattern) {
        if(StringUtils.isEmpty(date) || StringUtils.isEmpty(pattern)) return null;
        return toLocalDateTime(date, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalDateTime toLocalDateTime(String date, DateTimeFormatter formatter) {
        if(StringUtils.isEmpty(date) || null == formatter) return null;
        return LocalDateTime.parse(date, formatter);
    }
        
    public static Date toDate(LocalDateTime dateTime) {
        if(null == dateTime) return null;
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static LocalDateTime toLocalDateTime(Date date) {
        if(null == date) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    
    /*
     * Date는 가급적 지양
     */
    public static String toString(Date date, String formatter) {
        if(null == date) return null;
        SimpleDateFormat transFormat = new SimpleDateFormat(formatter);
        return transFormat.format(date);
    }
    
	/**
	 * 요일명 가져오기
	 * locale parameter 넘겨줄 시 해당 locale의 요일명으로 반환
	 * 
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
	 * 
	 * @return
	 */
	public static ZonedDateTime now() {
		return ZonedDateTime.now(DEFAULT_ZONE);
	}
	
	public static String now(String pattern) {
		return toString(now(), pattern);
	}
	
	public static String now(DateTimeFormatter formatter) {
        return toString(now(), formatter);
    }
	
	/**
	 * 날짜 기준으로 생일을 적용하여 나이를 계산
	 * 
	 * @param birthday
	 * @param current
	 * @return int
	 */
	public static int getAge(ZonedDateTime birthday, ZonedDateTime current) {
		return (int) ChronoUnit.YEARS.between(birthday, current);
	}
	
	/**
	 * get difference of days
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long diffOfDays(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());
	}
	
	/**
	 * get difference of hours
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long diffOfHours(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.HOURS.between(start.toInstant(), end.toInstant());
	}
	
	/**
	 * get difference of minutes
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long diffOfMinutes(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.MINUTES.between(start.toInstant(), end.toInstant());
	}
	
	/**
	 * get difference of seconds
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long diffOfSeconds(ZonedDateTime start, ZonedDateTime end) {
		return ChronoUnit.SECONDS.between(start.toInstant(), end.toInstant());
	}
	
	public static long diffOfSeconds(Date l, Date r) {
		return Math.abs(l.getTime() - r.getTime())/1000;
	}
	
	public static long diffOfSeconds(LocalDateTime l, LocalDateTime r) {
		return Duration.between(l, r).getSeconds();
	}
	
	public static long diffOfSeconds(Calendar l, Calendar r) {
		return Math.abs(l.getTimeInMillis()-r.getTimeInMillis())/1000;
	}
	
	/**
	 * 입력받은 시간(분) 정보로 ZoneId 반환
	 * 
	 * @param minutes
	 * @return
	 */
	public static ZoneId getZoneIdOfMinutes(long minutes) {
		int hours = (int)(minutes / 60);
		return ZoneOffset.ofHours(hours);
	}
	
	public static double offset(Date l, Date r) {
		return Math.abs(l.getTime() - r.getTime())/1000;
	}
	
	public static double offset(LocalDateTime l, LocalDateTime r) {
		return Duration.between(l, r).getSeconds();
	}
	
	public static double offset(Calendar l, Calendar r) {
		return Math.abs(l.getTimeInMillis()-r.getTimeInMillis())/1000;
	}

	/***
     * 초단위를 문자열로 변환. 
     * 
     * @param sec : 초
     * @param fmt : 포멧팅 문자열({0}: day, {1}: hour, {2}: min, {3}: sec)
     * @return String
     */
	public static String formatTime(long sec, String fmt) {
		long min = sec/60;
		long hour = min/60;
		long day = hour/24;
		
		return MessageFormat.format(fmt, day, hour%24, min%60, sec%60);
	}
	
	public static boolean isTimeout(ZonedDateTime timeout) {
		return null == timeout ? true : ZonedDateTime.now().compareTo(timeout) >= 0;
	}
	
	public static boolean isTimeout(LocalDateTime timeout) {
		return null == timeout ? true : LocalDateTime.now().compareTo(timeout) >= 0;
	}
	
	/**
     * 현재 년
     * 
     * @return int
     */
    public static int getYear()  {
        return now().getYear();
     }
    
    /***
     * 현재 월
     * 
     * @return int
     */
    public static int getMonth() {
        return now().getMonth().getValue();
    }
    
    /***
     * 현재 일
     * 
     * @return int
     */
    public static int getDay() {
        return now().getDayOfMonth();
    }
    
    /***
     * 현재 요일
     * 
     * @return String
     */
    public static String getWeek() {
        String [] week = {"","일","월","화","수","목","금","토"};
        return getWeek(week); 
    }
    
    public static String getWeek(String [] week) {
        return week[now().getDayOfWeek().getValue()]; 
    }
    
    /***
     * 현재달 주 (1주, 2주 ...)
     * 
     * @return int
     */
    public static int getCurrentWeek(){
        Calendar cal = Calendar.getInstance();
        int result = cal.get(Calendar.WEEK_OF_MONTH) ;
        return result;
    }
    
    /***
     * 해당년월의 마지막 날짜
     * 
     * @param nYear : 년도
     * @param nMonth : 월
     * @return int
     */
    public static int getLastDay(int nYear, int nMonth){
        GregorianCalendar cld = new GregorianCalendar (nYear, nMonth - 1, 1);
        int result = cld.getActualMaximum(Calendar.DAY_OF_MONTH);
        return result;
    }
    
    /***
     * 해당년월의 첫번째 날짜의 요일(1:SUNDAY, 2:MONDAY...)
     * 
     * @param nYear
     * @param nMonth
     * @return int
     */
    public static int getFirstWeekday(int nYear, int nMonth){
        GregorianCalendar cld = new GregorianCalendar (nYear, nMonth - 1, 1);
        int result = cld.get(Calendar.DAY_OF_WEEK);
        return result;
    }
    
    /***
     * 해당년월의 주의 개수
     * 
     * @param nFristWeekday : 그 달의 첫째날의 요일
     * @param nToDay : 그 달의 날짜 수
     * @return int
     */
    public static int getWeekCount(int nFristWeekday, int nToDay){
        int nCountDay = nFristWeekday + nToDay - 1;
        int result = (nCountDay / 7);
        if ((nCountDay % 7) > 0) {
            result++;
        }
        return result;
    }
    
    /***
     * 시작일에서 마지막일 까지 일일 단위 일자 목록
     * 
     * @param first : 시작일
     * @param last : 마지막일
     * @return List<LocalDate>
     */
	public static List<LocalDate> range(LocalDate first, LocalDate last) {
		List<LocalDate> ret = new ArrayList<>();
		
		for(LocalDate it = first; it.isBefore(last); it=it.plusDays(1)) {
		    ret.add(it);
		}
		
		return ret;
	}
}

