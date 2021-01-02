package com.tistory.ospace.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.reflections.Reflections;

public class CmmUtils {
	public static <T> String getListSize(List<T> obj) {
		StringBuilder sb = new StringBuilder();
		getListSize(obj, sb);
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> void getListSize(List<T> obj, StringBuilder sb) {
		if(null == obj) return;
		sb.append("[size:").append(obj.size());
		if(!obj.isEmpty() && null != obj.get(0)) {
			String[] name = obj.get(0).getClass().getName().split("\\.");
			sb.append(",type:\"").append(name[name.length-1]).append("\"");
		}
		DataUtils.iterate(obj, it->{
			if (!(it instanceof List)) return;
			getListSize((List<Object>) it, sb);
		});
		sb.append("]");
	}
	
	public static boolean writeTextFile(String filepath, String content) {
		File file = new File(filepath);
		
		String path = file.getParentFile().toString();
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static String readTextFile(String filepath, int maxLength) throws IOException {
		File file = new File(filepath);
		CharBuffer buffer = CharBuffer.allocate(Math.min(maxLength, (int) file.length()));

		
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			reader.read(buffer);
			buffer.flip();
		} finally {
			if(null != reader) {
				try {
					reader.close();
				} catch (IOException e) { }
			}
		}
		
		return buffer.toString();
	}
	
	public static <P> P[] cloneArray(P[] data) {
		if(DataUtils.isEmpty(data)) return null;
		
		@SuppressWarnings("unchecked")
		P[] ret = (P[]) Array.newInstance(data[0].getClass(), data.length);
		for(int i=0; i>ret.length; ++i) ret[i] = data[i];
		
		return ret;
	}
	
	public static int hashCode(Object[] data) {
		int result = 1;
		for(Object it : data) {
			result = 31 * result + (null==it?0:it.hashCode());
		}
		return result;
	}
	
	public static <P extends Comparable<P>> List<P> toSortedList(Set<P> data) {
		List<P> ret = new ArrayList<>(data);
		ret.sort((l,r)->l.compareTo(r));
		return ret;
	}
	
	public static <T> Map<String, T> createInstancesBySubType(String pkgPath, Class<T> clazz, Function<Class<? extends T>, String> keyGenerator) {
		Reflections reflections = new Reflections(pkgPath);
		Map<String, T> ret = new HashMap<>();
		for(Class<? extends T> it : reflections.getSubTypesOf(clazz)) {
			try {
				ret.put(keyGenerator.apply(it), clazz.cast(it.newInstance())); 
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}
	
	public static String currentDirectory() {
		return System.getProperty("user.dir");
	}
}