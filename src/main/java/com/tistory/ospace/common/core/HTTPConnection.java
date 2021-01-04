package com.tistory.ospace.common.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.logging.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.ospace.common.util.DataUtils;
import com.tistory.ospace.common.util.StringUtils;

public class HTTPConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(HTTPConnection.class);
	
	//private final static String USER_AGENT = "Mozilla/5.0";
	
	//Ref: https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
	static public String send(String method, String urlStr, String body, Map<String,String> properties) {
		HttpURLConnection conn = null;
		try {
			
			if("GET".equals(method) && !StringUtils.isEmpty(body)) {
				urlStr = urlStr.concat("?").concat(body);
				body = "";
			}
			
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			//conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			//conn.setConnectTimeout(8000);
			//conn.setReadTimeout(3000);
			
			if(!DataUtils.isEmpty(properties)) {
				for(Entry<String,String> it : properties.entrySet()) {
					conn.setRequestProperty(it.getKey(), it.getValue());	
				}
			}
			
			if(StringUtils.isNotEmpty(body)) {
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.writeBytes(body);
				out.flush();
				out.close();
			}
			
			LOGGER.trace("http request  : urlStr[{}] body[{}]", urlStr, body);
		
			int resCode = conn.getResponseCode();
			
			LOGGER.trace("http response : resCode[{}]", resCode);
			
			if (200 == resCode) {
				String ret = getResponse(conn.getInputStream());
				LOGGER.trace("http response : data[{}]", ret);
				return ret;
			} else {
				throw new RuntimeException(resCode+" "+getResponse(conn.getErrorStream()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(null!=conn) conn.disconnect();
		}
	}
	
	//Ref: http://www.baeldung.com/java-http-request
	static public String get(String urlStr, Map<String, String> params) {
		String args = null == params ? null : DataUtils.reduce(params.keySet(), (r,it)->{
//			try {
				if (0 < r.length()) r.append("&");
				r.append(it).append("=").append(params.get(it));
//				r.append(URLEncoder.encode(it, "UTF-8"))
//				 .append("=")
//				 .append(URLEncoder.encode(params.get(it), "UTF-8"));
//			} catch (UnsupportedEncodingException e) {
//				throw new RuntimeException(e);
//			}
		}, new StringBuffer()).toString();

		
		return send("GET", urlStr, args, null);
	}
	
	static public String post(String urlStr, String body) {
		return send("POST", urlStr, body, null);
	}
	
	static public String actionSoap(String urlStr, String action, String body) {
		Map<String, String> properties = new HashMap<>();
		
		properties.put("SOAPAction",  action);
		properties.put("Content-Type", "text/xml; charset=utf-8");
		
		return send("POST", urlStr, body, properties);
	}
	
	private static String getResponse(InputStream inStrm) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inStrm, "UTF8"));
			StringBuffer ret = new StringBuffer();
			char[] buf = new char[2048];
			int len = 0;
			while(0<=(len=in.read(buf))) {
				ret.append(buf, 0, len);
			}
			in.close();

			return ret.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}