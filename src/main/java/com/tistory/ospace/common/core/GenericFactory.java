package com.tistory.ospace.common.core;

public class GenericFactory<T> {
	private Class<T> clazz;
	
	public T create() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}
}
