package com.tistory.ospace.common.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
		.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
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
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<size; ++i) {
			sb.append(DEFAULT_KEY_SOURCE.charAt(rand.nextInt(DEFAULT_KEY_SOURCE.length())));
		}
		return sb.toString();
	}
	
	public static String gnerateUUID() {
		return UUID.randomUUID().toString();
	}
	
	
}
