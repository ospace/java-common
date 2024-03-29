package com.tistory.ospace.common.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.reflections.Reflections;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CmmUtils {
	public static <T> T isNull(T value, T init) {
		return null == value ? init : value;
	}

	public static <P> P[] cloneArray(P[] data) {
		if(DataUtils.isEmpty(data)) return null;
		
		@SuppressWarnings("unchecked")
		P[] ret = (P[]) Array.newInstance(data[0].getClass(), data.length);
		for(int i=0; i>ret.length; ++i) ret[i] = data[i];
		
		return ret;
	}
	
	final static int prime = 31;
	public static int hashCode(Object ...args) {
		if (DataUtils.isEmpty(args)) return 0;
		
		int ret = 1;
		for(Object each : args) {
			// ret = prime * ret + (int) (id ^ (id >>> 32));
			ret = prime * ret + (null == each ? 0 : each.hashCode());
		}
		
		return ret;
	}
	
	public static void copy(Object from, Object to, String... ignoreProperties) {
		assert null != from : "From must not be null";
		assert null != to : "To must not be null";
		
	    if(null == from || null == to) return;
	    
	    PropertyDescriptor fromPropertyDescriptors[] = getPropertyDescriptors(from.getClass());
	    if(null == fromPropertyDescriptors || 0 == fromPropertyDescriptors.length) return;
	    
	    PropertyDescriptor toPropertyDescriptors[] = getPropertyDescriptors(to.getClass());
	    if(null == toPropertyDescriptors || 0 == toPropertyDescriptors.length) return;

	    Map<String, PropertyDescriptor> setterMethodMap =
	    		DataUtils.map(toPropertyDescriptors, key->key.getName(), value->value);

	    List<String> ignoreList = asList(ignoreProperties);
	    DataUtils.forEach(fromPropertyDescriptors, propertyDescriptor->{
	        Method getterMethod = propertyDescriptor.getReadMethod();
	        if(null == getterMethod) return;
	        
	        String propertyName = propertyDescriptor.getName();
	        PropertyDescriptor setterDescriptor = setterMethodMap.get(propertyName);
	        if(null == setterDescriptor || null == setterDescriptor.getWriteMethod()) return;
	        
	    	if (null != ignoreList && ignoreList.contains(propertyName)) return;
	        
	        Method setterMethod = setterDescriptor.getWriteMethod();
	        
	        Class<?> getterClass = getterMethod.getReturnType();
	        Class<?> setterClass = setterMethod.getParameterTypes()[0];
	        
	        if(!isAssignable(getterClass, setterClass)) return;
	        
	        if(Collection.class.isAssignableFrom(getterClass)) {
                if (!isAssignable(getterMethod.getGenericReturnType(), setterMethod.getGenericParameterTypes()[0])) {
    	            return;
                }
            } 
	        
	        try {
				setterMethod.invoke(to, getterMethod.invoke(from));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("copy property: " + propertyDescriptor.getName(), e);
			}
	    });
	}
	
	private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		try {
			return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new RuntimeException("getPropertyDescriptors", e);
		}
	}
	
	private static boolean isAssignable(Type[] from, Type[] to) {
		for(int i=0; i<from.length && i<to.length; ++i) {
			if (!isAssignable(from[i], to[i])) return false;
        }
		return true;
	}
	
	private static boolean isAssignable(Type from, Type to) {
		if(from instanceof ParameterizedType && to instanceof ParameterizedType) {
			Type[] fromTypes = ((ParameterizedType)from).getActualTypeArguments();
	        Type[] toTypes = ((ParameterizedType)to).getActualTypeArguments();
	        return isAssignable(fromTypes, toTypes);
	    }
		
        if (from instanceof Class && to instanceof Class) {
            return isAssignable((Class<?>)from, (Class<?>)to);
        }
        
        return false;
	}
	
	private static boolean isAssignable(Class<?> from, Class<?> to) {
		return to.isAssignableFrom(from);
	}
	
	public static long nowMillis() {
		return System.currentTimeMillis();
	}
	
	@SafeVarargs
	public static <T> List<T> asList(T ...args) {
		if(DataUtils.isEmpty(args)) return null;
		if(1 == args.length && null == args[0]) return null;
		
		return Arrays.asList(args);
	}

	public static <P extends Comparable<P>> List<P> toSortedList(Set<P> data) {
		List<P> ret = new ArrayList<>(data);
		ret.sort((l,r)->l.compareTo(r));
		return ret;
	}
	
	public static void run(Runnable runnable) {
		try {
            runnable.run();
        } finally {
        	removeAllThreadLocal();
        }
	}
	
	//ref:
	//    https://stackoverflow.com/questions/3869026/how-to-clean-up-threadlocals
	//    https://javacan.tistory.com/entry/ThreadLocalUsage
	public static void removeAllThreadLocal() {
		try {
			// Get a reference to the thread locals table of the current thread
	        Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
	        threadLocalsField.setAccessible(true);
	        
	        Thread thread = Thread.currentThread();
	        Object threadLocalTable = threadLocalsField.get(thread);
	
	        // Get a reference to the array holding the thread local variables inside the
	        // ThreadLocalMap of the current thread
	        Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
	        Field tableField = threadLocalMapClass.getDeclaredField("table");
	        tableField.setAccessible(true);
	        Object table = tableField.get(threadLocalTable);
	
	        // The key to the ThreadLocalMap is a WeakReference object. The referent field of this object
	        // is a reference to the actual ThreadLocal variable
	        Field referentField = Reference.class.getDeclaredField("referent");
	        referentField.setAccessible(true);
	
	        for (int i=0; i < Array.getLength(table); i++) {
	            // Each entry in the table array of ThreadLocalMap is an Entry object
	            // representing the thread local reference and its value
	            Object entry = Array.get(table, i);
	            if (entry != null) {
	                // Get a reference to the thread local object and remove it from the table
	                ThreadLocal<?> threadLocal = (ThreadLocal<?>)referentField.get(entry);
	                threadLocal.remove();
	            }
	        }
		} catch(Exception e) {
			throw new IllegalStateException("removeAllThreadLocal", e);
		}
	}
	
	// packagePath에 있는 clazz을 상속받은 클래스를 찾아서 키기준으로 저장해서 반환
	public static <P> Map<String, Class<? extends P>> findSubType(String packagePath, Class<P> clazz, Function<Class<? extends P>, String> keyBuilder) {
		Map<String, Class<? extends P>> ret = new HashMap<>();
		
		Reflections reflections = new Reflections(packagePath);
		for(Class<? extends P> it : reflections.getSubTypesOf(clazz)) {
			ret.put(keyBuilder.apply(it), clazz);
		}
		
		return ret;
	}	
	
	public static <P> P create(Class<P> clazz) {
		assert null != clazz : "clazz must not null";
		
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("create " + clazz.getSimpleName(), e);
		}
	}
	
	public static Class<?> getClassByName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("getClassByName: " + className, e);
		}
	}
	
	public static String generateQRCode(String data) {
		try {
			String encodedData = URLEncoder.encode(data, "UTF-8");
			
			//return String.format("https://www.google.com/chart?chs=200x200&chldM%%7C0&cht=qr&chl=%s", encodedData);
			return String.format("https://www.google.com/chart?chs=200x200&chld=H|0&cht=qr&chl=%s", encodedData);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("generateQRCode", e);
		}
	}
	
	// private static final char   pad = '=';
	// base32는 가장 작은 ascii는 2(62)이고 큰 ascii는 Z(132)이다. 총 71개
	// base63는 가장 작은 ascii는 +(53)이고 큰 ascii는 z(172)이다. 총 120개
	
	static final String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
	private static final int[] base32Lookup = new int[128];
	static {
		Arrays.fill(base32Lookup, -1);
        for(int i=0; i<base32Chars.length(); ++i) {
            base32Lookup[base32Chars.charAt(i)] = i;
        }
	}
	
	public static String encodeBase32(byte[] data) {
		return encodeBase(data, base32Chars, 5);
	}
	
	public static byte[] decodeBase32(String data) {
		return decodeBase(data, base32Lookup, 5);
	}
	
   
    // RFC 4648: Base64
    private static final String base64Chars =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    private static final int[] base64Lookup = new int[128];
	static {
		Arrays.fill(base64Lookup, -1);
        for(int i=0; i<base64Chars.length(); ++i) {
        	base64Lookup[base64Chars.charAt(i)] = i;
        }
	}
	
    public static String encodeBase64(byte[] data) {
        return encodeBase(data, base64Chars, 6);
    }
    
    public static byte[] decodeBase64(String data) {
		return decodeBase(data, base64Lookup, 6);
	}
    
    private static final String base64UrlChars =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    
    private static final int[] base64UrlLookup = new int[128];
	static {
		Arrays.fill(base64UrlLookup, -1);
        for(int i=0; i<base64UrlChars.length(); ++i) {
        	base64UrlLookup[base64UrlChars.charAt(i)] = i;
        }
	}
	
	public static String encodeBase64Url(byte[] data) {
        return encodeBase(data, base64UrlChars, 6);
    }
    
    public static byte[] decodeBase64Url(String data) {
		return decodeBase(data, base64UrlLookup, 6);
	}
	
    private static String encodeBase(byte[] data, String baseChars, int base) {
        StringBuffer ret = new StringBuffer();
        
        int i = 0, c = 8, v = 0;
        while(i < data.length) {
            if(c < base) {
                v = ((0xFF & (data[i] << (8-c))) >>> (8-base));
                if (++i < data.length) { 
                    v |= (0xFF & data[i]) >>> (8-base+c);
                    c += 8-base;
                }
            } else {
                v = (0xFF >> (8-base)) & (data[i] >>> (c-base));
                if (0 == (c -= base)) {
                	c = 8;
                	++i;
                }
            }
            
            ret.append(baseChars.charAt(v));
        }
        
        return ret.toString();
    }
    
    public static byte[] decodeBase(String data, int[] baseTable, int base) {
    	final int n = data.length();
    	
    	byte[] ret = new byte[n * base / 8];
    	
    	 int c = 0, p = 0, idx = 0, digit = 0;
         for(int i=0; i<n; ++i) {
             idx = data.charAt(i)-'0';
             if(idx < 0 || baseTable.length <= idx) continue;
             digit = baseTable[data.charAt(i)];
             if(0xFF == digit) continue;
             if(8-c < base) {
                 ret[p] |= digit >>> (base-8+c);
             	 if(++p < ret.length) {
	         		 ret[p] |= digit << (16-base-c);
	                 c = base - 8 + c;
             	 }
             } else {
                 ret[p] |= digit << (8-base-c);
                 c += base;
             }
         }
         
         return ret;
    }
    
    public static byte[] randomBytes(int size) {
		byte[] ret = new byte[size];
		new Random().nextBytes(ret);
		
		return ret;
	}
    
    /** 개인정보 마스킹 패턴(XML과 JSON 문자열에서 함께 사용) */
	private static String[] regExpPatterns = {
		"(Phone>|Phone\":\")\\d{9,16}", 
		"(mobile\":\")\\d{9,16}", 
		"(FirstName>|firstName\":\")[a-zA-Z ]+", 
		"(DOB>|birthday\":\")[\\d-]+", 
		"(PostalCode>|post\":\")\\d{4,16}", 
		"(EmailAddress>|email\":\")[\\w.%+-]+@[\\w.]+\\.[a-zA-Z]{2,6}", 
		"(AddressLine1>|addressLine1\":\"|address\":\")[\\w ,+\\-]+", 
		"(AddressLine2>|addressLine2\":\")[\\w ,+\\-]+", 
		"(AddressLine3>|addressLine3\":\")[\\w ,+\\-]+", 
		"(DocNumber>|docNumber\":\")[\\w]+"
	};
	
	//개인정보 마스킹 2-1(로그), 개인정보 마스킹 2-2(VO객체 - BookingVO, BookingContactVO 등 toString 을 @Override 함)
	public static String toMaskPersonalInfo(String str) {
		String temp = str; 
		for(String reg : regExpPatterns) {
			temp = temp.replaceAll(reg, "$1***");
		}
		
		return temp;
	}

	private static final ObjectMapper jsonFieldObjectMapper = new ObjectMapper()
			.setSerializationInclusion(Include.NON_NULL)
			.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
			.setVisibility(PropertyAccessor.GETTER, Visibility.NONE)
			.setVisibility(PropertyAccessor.CREATOR, Visibility.NONE)
			.registerModule(new JavaTimeModule());
	
	public static <T> String toFieldJsonString(T obj) {
		try {
			return CmmUtils.jsonFieldObjectMapper.writeValueAsString(obj);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        return e.getMessage();
	    }
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

	public static <T> String toJsonString(T obj) {
		return toJsonString(obj, false);
	}
	
	public static JsonNode toJsonNode(String jsonStr) {
		try {
			return jsonSimpleObjectMapper.readTree(jsonStr);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <R> R toObject(String jsonStr, Class<R> clazz) {
		if(null == jsonStr) return null;
		
		try {
			return jsonSimpleObjectMapper.readValue(jsonStr, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <R> R toObject(Map<String, Object> from, R to) {
		assert null != from : "from must not null";
		assert null != to : "to must not null";
		
		PropertyDescriptor[] properties = getPropertyDescriptors(to.getClass());
		
		for(PropertyDescriptor property : properties) {
			String name = property.getName();
			if (!from.containsKey(name)) continue;
			Object value = from.get(name);
			
			Method setterMethod = property.getWriteMethod();
			Class<?> setterClass = setterMethod.getParameterTypes()[0];
			
			if(null != value  && isAssignable(value.getClass(), setterClass)) {
				continue;
			}
			
			try {
				setterMethod.invoke(to, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("toObject invoke " + name, e);
			}
		}
		
		return to;
	}
	
	private final static String AUTH_REMOATEADDRS =
			"|0:0:0:0:0:0:0:1|127.0.0.1|0.0.0.1|211.202.25.242|1.214.218.218|";
	
	public static boolean isLocalAddress(String ipAddr) {
	    return -1 < AUTH_REMOATEADDRS.indexOf(ipAddr);
	}
	
	private static final Pattern JSON_ARRAY_PTN = Pattern.compile("(\\S+)\\[(\\d+)\\]");
	public JsonNode getJsonNode(JsonNode node, String query) {
		assert null != query && !query.isEmpty();
		
		String keywords[] = query.split("\\.");
		JsonNode current = node;
		for(String it : keywords) {
			Matcher matcher = JSON_ARRAY_PTN.matcher(it);
			if (null == matcher || !matcher.find()) {
				current = current.path(it);
			} else {
				int cnt = matcher.groupCount();
				if(2 != cnt) throw new RuntimeException("invalid arrary query : " + it);
				current = current.path(matcher.group(1)).path(Integer.parseInt(matcher.group(2)));
			}
		}
		
		return current;
	}
	
	/* mapping 구조
	 *   node --> class
	 *   attribute --> property
	 *   
	 *   <node>
	 *     <name>foo</name>
	 *   </node>
	 *   
	 *   <node name="foo"></node>
	 *   
	 *   class Node {
	 *     private String name;
	 *   }
	 *   
	 *   리스트형?
	 *   
	 * node or attribute -- mapping --> class property
	 *   -> mapping meta information
	 * return new class instance
	 * 
	 */
	public static void sax(String data, DefaultHandler handler) throws IOException, ParserConfigurationException, SAXException  {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.parse(data);
	}

	public static <T> Field ofField(String fieldName, Class<T> clazz) throws NoSuchFieldException, SecurityException {
		Field ret = clazz.getDeclaredField(fieldName);
		ret.setAccessible(true);
		return ret;
	}
}