package com.tistory.ospace.common.indextable.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tistory.ospace.common.indextable.core.Table;


public class QueryUtils {
	public static <T> List<Collection<Integer>> collect(Table<T> tbl, Collection<Query<T>> queries, List<Object> args) {
		List<Collection<Integer>> result = new ArrayList<>();
		for(Query<T> q : queries) {
			result.add(q.apply(tbl, args));
		}
		return result;
	}
	
	public static <T> Collection<T> intersection(List<Collection<T>> set) {
		for(Collection<T> it: set) if (null == it) return null;
		
		set.sort((l,r)->Integer.compare(l.size(),r.size()));
		
		Collection<T> small = set.get(0);
		List<Collection<T>> others = set.subList(1, set.size());
		
		Collection<T> ret = new LinkedHashSet<>();
		for(T it : small) {
			if(others.stream().anyMatch(it2->!it2.contains(it))) continue;
			ret.add(it);
		}
		
		return ret;
	}
	
	public static <T> Collection<T> union(List<Collection<T>> set) {
		Collection<T> ret = new LinkedHashSet<>();
		for(Collection<T> it : set) ret.addAll(it);
		return ret;
	}
	
	public static Collection<Integer> getRange(final Map<Object, Set<Integer>> index, final Object fromValue, final Object toValue, boolean inclusive) {
		if(null == fromValue && null == toValue) return null;
		
		
		if(!(index instanceof TreeMap)) {
			throw new RuntimeException("Range is TREE index type only supported");
		}
	
		final TreeMap<Object, Set<Integer>> treeIndex = (TreeMap<Object, Set<Integer>>)index;
		NavigableMap<Object, Set<Integer>> subTree = null;
		
		if(null == fromValue) {
			subTree = treeIndex.headMap(toValue, inclusive);
		} else if (null == toValue) {
			subTree = treeIndex.tailMap(fromValue, inclusive);
		} else {
			subTree = treeIndex.subMap(fromValue, inclusive, toValue, inclusive);
		}

		Collection<Integer> result = new HashSet<>();
		subTree.values().forEach(it->result.addAll(it));
		return result;
	}
	
	public static <T> Collection<Integer> findIn(String index, Table<T> tbl, List<?> values) {
		Map<Object, Set<Integer>> indexMap = tbl.getIndex(index);
		Collection<Integer> result = new HashSet<>();
		for(Object it : values) {
			Set<Integer> val = indexMap.get(it);
			if(null == val) continue;
			result.addAll(val);
		}
		return result;
	}
	
	public static Collection<Integer> getValue(final Map<Object, Set<Integer>> index, Object value) {
		return index.get(value);
	}
	
	private static Pattern holderPattern = Pattern.compile("^\\s*\\{(\\w+)\\}\\s*$");
	public static Holder holderOf(Object val) {
		if (val instanceof String) {
			String str = (String) val;
			Matcher matcher = holderPattern.matcher(str);
			if (matcher.find()) {
				return new HolderArgument(Integer.valueOf(matcher.group(1)));
			} 
		}
		return new HolderValue(val);
	}
}
