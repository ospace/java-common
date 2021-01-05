package com.tistory.ospace.common.indexedtable.query;

import java.util.List;

public class HolderValue implements Holder {
	private Object value;
	
	public HolderValue(Object value) {
		this.value = value;
	}

	@Override
	public Object get(List<Object> args) {
		return value;
	}
}
