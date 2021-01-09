package com.tistory.ospace.common.core;

import com.tistory.ospace.common.util.DataUtils;

//import org.apache.logging.log4j.message.ParameterizedMessage;
/**
 * BaseException 은 상위클래스 
 * 
 * @author ospace (ospace@trgtech.co.kr)
 * @since 2018.05.10
 * @version 1.0
 * @see Exception
 */

public class BaseException extends RuntimeException {
	
	private static final long serialVersionUID = -8936079842059526426L;
	private String  system;
	private String  flow;
	private Integer status;

	public BaseException(Exception cause) {
		super(cause);
	}
	
	public BaseException(String system, Integer status, String msg) {
		super("["+system+"] "+msg);
		this.system = system;
		this.status = status;
		
		setFlow(this.getStackTrace());
	}
	
	public BaseException(String system, Integer status, Exception cause) {
		super(cause);
		this.system = system;
		this.status = status;
		
		setFlow(cause.getStackTrace());
	}

	public BaseException(String system, Integer status, String msg, Exception cause) {
		super("["+system+"] "+msg, cause);
		this.system = system;
		this.status = status;
		
		setFlow(cause.getStackTrace());
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public String getSystem() {
		return system;
	}
	
	public String getFlow() {
		return flow;
	}
	
	private void setFlow(StackTraceElement[] trace) {
	    this.flow = createFlow(trace);
	}
	
	public static String createFlow(StackTraceElement[] trace) {
		return String.join(">", DataUtils.map(trace, it->{
			return it.getClassName().concat(".").concat(it.getMethodName()).concat("()");
		}));
	}
}
