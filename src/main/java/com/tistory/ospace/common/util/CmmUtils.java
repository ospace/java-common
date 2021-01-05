package com.tistory.ospace.common.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.reflections.Reflections;

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
	
	public static int hashCode(Object ...args) {
		if(DataUtils.isEmpty(args)) return 0;
		
		int ret = 1;
		for(Object it : args) {
			ret = 31 * ret + (null==it?0:it.hashCode());
		}
		
		return ret;
	}
	// packagePath에 있는 clazz을 상속받은 클래스를 찾아서 인스탄스화시켜서 키기준으로 저장해서 반환
	public static <T> Map<String, T> createBySubType(String packagePath, Class<T> clazz, Function<Class<? extends T>, String> keyGenerator) {
		Reflections reflections = new Reflections(packagePath);
		Map<String, T> ret = new HashMap<>();
		for(Class<? extends T> it : reflections.getSubTypesOf(clazz)) {
			try {
				ret.put(keyGenerator.apply(it), clazz.cast(it.newInstance())); 
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("createBySubType: " + packagePath + ", " + clazz, e);
			}
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

	    List<String> ignoreList = DataUtils.asList(ignoreProperties);
	    DataUtils.iterate(fromPropertyDescriptors, propertyDescriptor->{
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
}