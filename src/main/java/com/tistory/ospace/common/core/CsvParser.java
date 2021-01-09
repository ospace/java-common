package com.tistory.ospace.common.core;

import java.io.IOException;
import java.io.OutputStream;

/* TODO 데이터 처리가 줄단위로 파싱과 저장되는 것을 전체 데이터로 수정
 * 
 */
public class CsvParser {
	//private static final char CR = '\n';
	private static final char SEP = ',';

	private byte[] data;
	private int i=0;
	
	public static CsvParser of(byte[] data) {
		return new CsvParser(data);
	}
	
	public static CsvParser of(String data) {
		return new CsvParser(data.getBytes());
	}
	
	private CsvParser(byte[] data) {
		this.data = data;
	}
	
	public String next() {
		if(i >= data.length) return null;
		
		StringBuilder sb = new StringBuilder();
		int b = i;
		for(int n=data.length; i<n; ++i) {
			int c=data[i];
			if('\\' == c) {
				if(b != i) {
					sb.append(new String(data, b, i-b));
				}
				if(++i >= n) break;
				b=i;
				continue;
			}
			if(SEP == c) break; 
		}
		
		if(b != i) {
			sb.append(new String(data, b, i-b));
		}
		++i;
		
		return sb.toString();
	}
	
	//public String[] nextLine() {
	//}

	public int write(OutputStream out) throws IOException {
		int i = 0;
		for(int n=data.length; i<n; ++i) {
			byte c = data[i];
			switch(c) {
			case ',':
				out.write('\\');
				out.write(',');
				break;
			case '\\':
				out.write('\\');
				out.write('\\');
				break;
			default:
				out.write(c);
			}
		}
		
		return i;
	}
}
