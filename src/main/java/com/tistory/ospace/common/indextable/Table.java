package com.tistory.ospace.common.indextable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tistory.ospace.common.indextable.query.Query;


public interface Table<T> {
	public Map<Object, Set<Integer>> getIndex(final String index);
	
	public T get(int idx);
	
	public int size();
	
	public ResultSet<T> query(Query<T> query);
	
	public ResultSet<T> query(Query<T> query, Object... args);
	
	public ResultSet<T> query(Query<T> query, List<Object> args);
}
