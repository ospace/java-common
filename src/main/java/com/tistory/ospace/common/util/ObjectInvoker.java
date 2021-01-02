package com.tistory.ospace.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class ObjectInvoker {
	private Object                target;
	private Class<?>              converter;
	private Map<Class<?>, Method> methodMap;
	
	public ObjectInvoker(Object target) {
		this.target    = target;
		this.converter = target.getClass();
		this.methodMap = DataUtils.map(
			DataUtils.filter(this.converter.getMethods(), it->isFilter(it)),
			key->extractKey(key.getParameterTypes()), val->val
		);
	}
	
	public <P> Object invoke(Class<P> clazz, Object... args) {
		try {
			Method method = methodMap.get(clazz);
			
			if(null == method) {
				throw new UnsupportedOperationException(converter.getSimpleName()+"("+clazz.getSimpleName()+")");
			}
			
			return method.invoke(target, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOperationException(converter.getSimpleName()+"("+clazz.getSimpleName()+"["+e.getClass().getSimpleName()+"])", e);
		}
	}
	
	protected abstract boolean isFilter(Method method);
	
	protected abstract Class<?> extractKey(Class<?>[] params);
}
