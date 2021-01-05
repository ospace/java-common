package com.tistory.ospace.common.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


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
	
	public static <T> String toJsonString(T obj) {
		return toJsonString(obj, false);
	}
	
	private static final ObjectMapper jsonSimpleObjectMapper = new ObjectMapper()
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) //없는 프로퍼티를 매핑 에러 처리
		.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
		.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) //프로퍼티에 빈문자열을 NULL로 매핑
		.registerModule(new JavaTimeModule())
		.setSerializationInclusion(Include.NON_NULL);
	public static <T> String toJsonString(T obj, boolean isPretty) {
		try {
			if (isPretty) {
				return jsonSimpleObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			} else {
				return jsonSimpleObjectMapper.writeValueAsString(obj);
			}
	    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        return e.getMessage();
	    }
	}
	
	private static final ObjectMapper jsonFieldObjectMapper = new ObjectMapper()
		.setSerializationInclusion(Include.NON_NULL)
		.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
		.setVisibility(PropertyAccessor.GETTER, Visibility.NONE)
		.setVisibility(PropertyAccessor.CREATOR, Visibility.NONE)
		.registerModule(new JavaTimeModule());
	public static <T> String toFieldJsonString(T obj) {
		try {
			return jsonFieldObjectMapper.writeValueAsString(obj);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        return e.getMessage();
	    }
	}
	
	public static JsonNode toJsonObject(String jsonStr) {
		try {
			return jsonSimpleObjectMapper.readTree(jsonStr);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <R> R toJsonObject(String jsonStr, Class<R> clazz) {
		if(null == jsonStr) return null;
		
		try {
			return jsonSimpleObjectMapper.readValue(jsonStr, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final String DEFAULT_KEY_SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static String generateKey(int size) {
		return generateKey(size, DEFAULT_KEY_SOURCE);
	}
	
	public static String generateKey(int size, String keySource) {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<size; ++i) {
			sb.append(keySource.charAt(rand.nextInt(keySource.length())));
		}
		return sb.toString();
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
}
