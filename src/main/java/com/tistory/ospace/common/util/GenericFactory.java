package com.tistory.ospace.common.util;

public class GenericFactory<T> {
	private Class<T> clazz;
	
	public T create() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}
}
