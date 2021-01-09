package com.tistory.ospace.common.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
	static final String STR_SEP = ",";
	

	public static boolean isEmpty(String str) {
		return null == str ? true : str.isEmpty();
	}
	
	public static String isEmpty(String val, String init) {
		return isEmpty(val) ? init : val;
	}
	
	public static <R> R isEmpty(String val, R init, Function<String, R> action) {
		return isEmpty(val) ? init : action.apply(val);
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static String[] split(String str) {
		if(isEmpty(str)) return null;
		return str.split(STR_SEP); 
	}
	
	public static String join(CharSequence delimiter, String... strs) {
		if(null == strs) return null;
		return String.join(delimiter, strs);
	}
	
	public static String join(String... strs) {
		return join(STR_SEP, strs);
	}
	
	public static String join(CharSequence delimiter, List<String> strs) {
		return join(delimiter, strs);
	}
	
	public static String join(List<String> strs) {
		if(null == strs) return null;
		return String.join(STR_SEP, strs);
	}
	
	public static String join(CharSequence delimiter, Collection<String> strs) {
		if(null == strs) return null;
		return String.join(delimiter, strs);
	}
	
	public static String join(Collection<String> strs) {
		return join(STR_SEP, strs);
	}
	
	private static final String DEFAULT_KEY_SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static String generateKey(int size) {
		return generateKey(size, DEFAULT_KEY_SOURCE);
	}
	
	private static final Random RANDOM = new Random();
	public static String generateKey(int size, String keySource) {
		char [] ret = new char[size];
		
		for(int i=0; i<size; ++i) {
			ret[i] = keySource.charAt(RANDOM.nextInt(size));
		}
		
		return new String(ret);
	}
	
	public static String newUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String newId() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	private static Pattern formatPattern = Pattern.compile("\\{(\\w+?\\)}");
	public static String format(String fmt, Object ...args) {
		StringBuffer ret = new StringBuffer();
		
		Matcher matcher = formatPattern.matcher(fmt);
		while(matcher.find()) {
		   for(int i=1; i<=matcher.groupCount(); ++i) {
		       Integer idx = Integer.parseInt(matcher.group(i));
		       if(args.length >= idx) continue;
		       Object val = args[idx];
		       matcher.appendReplacement(ret, null == val ? null : (String.class.equals(val.getClass()) ? (String)val : val.toString()));
		   }
		}
		
		return ret.toString();
	}
	
	public static String mapping(String str, Map<String,String> map) {
		StringBuffer ret = new StringBuffer();
		
		Matcher matcher = formatPattern.matcher(str);
		while(matcher.find()) {
		   for(int i=1; i<=matcher.groupCount(); ++i) {
			   String key = matcher.group(i);
		       String val = map.get(key);
		       
		       if(null == val) continue;
		       matcher.appendReplacement(ret, val);
		   }
		}
		
		return ret.toString();
	}
	
	public static String toString(ByteBuffer byteBuffer) {
		return toString(byteBuffer, Charset.forName("UTF-8"));
	}
	
	public static String toString(ByteBuffer byteBuffer, Charset charset) {
		return charset.decode(byteBuffer).toString();
	}
	
	public static ByteBuffer toByteBuffer(String str) {
		return toByteBuffer(str, Charset.forName("UTF-8"));
	}
	
	public static ByteBuffer toByteBuffer(String str, Charset charset) {
		return charset.encode(str);
	}
	
	public static boolean isKorean(String data) {
		if(isEmpty(data)) return false;
		
		return data.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
	}
	
	public static boolean isKorean2(String data) {
		if(isEmpty(data)) return false;
		for(int i=0; i<data.length(); ++i) {
			if (5 == Character.getType(data.charAt(i))) return true;
		}
		
		return false;
	}
	
	public static String replaceAll(String str, String from, String to) {
		StringBuffer sb = new StringBuffer();

		int idx = 0, pt = 0;

		while(0 < (pt = str.indexOf(from, idx))) {
			sb.append(str.substring(idx, pt)).append(to);
			idx = pt + from.length();
		}

		if(idx < str.length()) sb.append(str.substring(idx));
		
		return sb.toString();
	}
	
	public static String toHexString(byte[] bytes) {
		try (Formatter formatter = new Formatter()) {
			for (byte b : bytes) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
	}
	
	public static String leftPad(String value, int len, String padVal) {
		int padLen = null == value ? len : len - value.length();
		
		return len > 0 ? repeatString(padVal, padLen).concat(value) : value;
	}
	
	public static String repeatString(String value, int repeat) {
		String ret = "";
		for(int i=0; i<repeat; ++i) {
			ret = ret.concat(value);
		}
		return ret;
	}
	
	/**
	 * 특수문자 제거
	 * @param str
	 * @return
	 */
	public static String removeSpecialCharacters(String str) {
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z0-9\\s-,]";
		str = str.replaceAll(match, "");
		return str;
	}
	
	/**
	 * 정규식 체크
	 * @param pattern
	 * @param str
	 * @return
	 */
	public static boolean isRegex(String pattern, String str) {
		if(str == null) return false;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.find();
		
	}
	
	/**
	 * 숫자 체크
	 * @param pattern
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if(str == null) return false;
		return isRegex("^[0-9]+$", str);
	}
}
