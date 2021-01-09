package com.tistory.ospace.common.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.ospace.common.util.DataUtils;
import com.tistory.ospace.common.util.FileUtils;

//Ref: https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
//Ref: http://www.baeldung.com/java-http-request

public class HTTPConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(HTTPConnector.class);
	
	private static Charset CHARSET = Charset.forName("UTF-8");
	
	private String             url;
	private Map<String,String> properties;
	private Map<String,String> params;
	
	private int connectTimeout = 0;
	private int readTimeout = 0;
	
	
	public static HTTPConnector of(String url) {
		return new HTTPConnector(url);
		//.setProperty("User-Agent", USER_AGENT)
	}
	
	public HTTPConnector(String url) {
		this.url = url;
	}
	
	public HTTPConnector setProperty(String key, String value) {
		if(null == properties) {
			properties = new HashMap<>();
		}
		properties.put(key, value);
		
		return this;
	}
	
	public HTTPConnector setParam(String key, String value) {
		if(null == params) {
			params = new HashMap<>();
		}
		params.put(key, value);
		
		return this;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String get() {
		String args = null == params ? null : DataUtils.reduce(params.keySet(), (r,it)->{
			if (0 < r.length()) r.append("&");
			r.append(it).append("=").append(params.get(it));
//				r.append(URLEncoder.encode(it, "UTF-8"))
//				 .append("=")
//				 .append(URLEncoder.encode(params.get(it), "UTF-8"));
		}, new StringBuffer()).toString();
		
		return send("GET", args.getBytes(CHARSET));
	}
	
	public String post(String body) {
		return send("POST", body.getBytes(CHARSET));
	}

	private String send(String method, byte[] body) {
		HttpURLConnection conn = null;
		try {
			String urlStr = url;
			if("GET".equals(method) && !DataUtils.isEmpty(body)) {
				urlStr = urlStr.concat("?").concat(new String(body));
				body = null;
			}
			
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			//conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			if (0 < connectTimeout) {
				conn.setConnectTimeout(connectTimeout);
			}
			if (0 < readTimeout) {
				conn.setReadTimeout(readTimeout);
			}
			
			if(!DataUtils.isEmpty(properties)) {
				for(Entry<String,String> it : properties.entrySet()) {
					conn.setRequestProperty(it.getKey(), it.getValue());	
				}
			}
			
			if(!DataUtils.isEmpty(body)) {
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.write(body);
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
				throw new RuntimeException("["+resCode+"] "+getResponse(conn.getErrorStream()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(null!=conn) conn.disconnect();
		}
	}
	
	private static String getResponse(InputStream inStrm) {
		try {
			//BufferedReader in = new BufferedReader(new InputStreamReader(inStrm, "UTF8"));
			ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
			
			FileUtils.copy(inStrm, outBuffer);
			
			return outBuffer.toString();
		} catch (IOException e) {
			throw new RuntimeException("getResponse", e);
		}
	}
}
