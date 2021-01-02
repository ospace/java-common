package com.tistory.ospace.common.util;

public class Runtime extends BaseObject {
	long begin_time = 0;
	long end_time = 0;
	
	public static Runtime begin() {
		Runtime ret = new Runtime();
		ret.start();
		return ret;
	}
	
	public void start() {
		begin_time = System.currentTimeMillis();
		end_time = 0;
	}
	
	public void stop() {
		end_time = System.currentTimeMillis();
	}
	
	public long lap() {
		return System.currentTimeMillis() - begin_time;
	}
	
	public long get() {
		return end_time - begin_time;
	}
}