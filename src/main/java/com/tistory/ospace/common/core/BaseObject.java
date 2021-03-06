package com.tistory.ospace.common.core;

import com.tistory.ospace.common.util.CmmUtils;

public class BaseObject implements Cloneable {
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return CmmUtils.toJsonString(this);
	}
}