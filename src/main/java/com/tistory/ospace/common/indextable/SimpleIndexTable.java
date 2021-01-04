package com.tistory.ospace.common.indextable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import com.tistory.ospace.common.indextable.query.Query;
import com.tistory.ospace.common.indextable.query.Queryable;


public class SimpleIndexTable<T extends Object> implements Table<T>, Queryable<T>{
	private ArrayList<T>                               data = new ArrayList<>();
	private Map<String, Map<Object, Set<Integer>>>     indexes = new HashMap<>();
	private Map<String, Function<T, ? extends Object>> methods = new HashMap<>();
	
	public static <T> SimpleIndexTable<T> of(Class<T> clazz) {
		return new SimpleIndexTable<T>();
	}
	
	public SimpleIndexTable() {}
	
	public <R> void addIndex(String name, Function<T,R> getter) {
		addIndex(name, getter, IndexType.HASH);
	}
	
	public <R> void addIndex(String name, Function<T,R> getter, IndexType indexType) {
		methods.put(name, getter);
		indexes.put(name, createMap(indexType));
	}
	
/*	public <R> void addIndex(String name, Class<R> clazz, Function<T,Collection<R>> getter) {
		addIndex(name, clazz, getter, IndexType.HASH);
	}
	
	public <R> void addIndex(String name, Class<R> clazz, Function<T,Collection<R>> getter, IndexType indexType) {
		methods.put(name, arg->{
			Collection<R> data = getter.apply(arg);
			return data;
		});
		indexes.put(name, createMap(indexType));
	}*/
	
	private Map<Object, Set<Integer>> createMap(IndexType indexType) {
		switch(indexType) {
		case HASH: return new HashMap<>();
		case TREE: return new TreeMap<>();
		case LINKED: return new LinkedHashMap<>();
		}
		
		throw new RuntimeException("not supported index type : " + indexType.toString());  
	}
	
	public void add(T item) {
		if(null == item) return;
		
		this.data.add(item);
		Integer idx = this.data.size()-1;
		this.methods.forEach((name, method)->{
			Object key = method.apply(item);
			if (key instanceof Collection) {
				for(Object it : Collection.class.cast(key)) {
					addValue(name, it, idx);
				}
			} else {
				addValue(name, key, idx);	
			}
		});
	}
	
	public void addAll(List<T> data) {
		this.data.ensureCapacity(data.size()+this.data.size());
		for(T it : data) add(it);
	}

	@Override
	public ResultSet<T> query(Query<T> query) {
		Collection<Integer> res = query.apply(this, null);
		return new ResultSet<T>(res, this);
	}


	@Override
	public ResultSet<T> query(Query<T> query, Object... args) {
		return query(query, Arrays.asList(args));
	}

	@Override
	public ResultSet<T> query(Query<T> query, List<Object> args) {
		Collection<Integer> res = query.apply(this, args);
		return new ResultSet<T>(res, this);
	}
	
	@Override
	public Map<Object, Set<Integer>> getIndex(String index) {
		Map<Object, Set<Integer>> result = indexes.get(index);
		if(null == result) throw new RuntimeException("unknown index : " + index);
		return result;
	}
	
	@Override
	public int size() {
		return data.size();
	}
	
	@Override
	public T get(int idx) {
		return data.get(idx);
	}
	
	private void addValue(String index, Object key, Integer value) {
		Map<Object, Set<Integer>> col_idx = getIndex(index);
		Set<Integer> vals = col_idx.get(key);
		if(null == vals) {
			col_idx.put(key, vals = new HashSet<>());
		}
		vals.add(value);
	}

}
