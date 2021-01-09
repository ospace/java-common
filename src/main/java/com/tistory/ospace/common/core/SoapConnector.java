package com.tistory.ospace.common.core;

public class SoapConnector {
	private HTTPConnector conn;
	
	public SoapConnector(String urlStr) {
		conn = new HTTPConnector(urlStr)
				.setProperty("Content-Type", "text/xml; charset=utf-8");
	}
	
	public String send(String action, String body) {
		conn.setProperty("SOAPAction", action);
		return conn.post(body);
	}
}
