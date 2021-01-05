package com.tistory.ospace.common.indexedtable.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryOp {
	@SafeVarargs
	public static <T> Query<T> and(Query<T>... queries) {
		return and(Arrays.asList(queries));
	}
	
	public static <T> Query<T> and(Collection<Query<T>> queries) {
		return (tbl, args)->{
			List<Collection<Integer>> result = QueryUtils.collect(tbl, queries, args);
			return QueryUtils.intersection(result);
		};
	}
	
	@SafeVarargs
	public static <T> Query<T> or(Query<T>... queries) {
		return or(Arrays.asList(queries));
	}
	
	public static <T> Query<T> or(Collection<Query<T>> queries) {
		return (tbl, args)->{
			List<Collection<Integer>> result = QueryUtils.collect(tbl, queries, args);
			return QueryUtils.union(result);
		};
	}
	
	public static <T> Query<T> not(Query<T> query) {
		return (tbl, args)->{
			Collection<Integer> res = query.apply(tbl, args);
			int size = tbl.size();
			Collection<Integer> result = new HashSet<>();
			for(int i = 0; i<size; ++i) {
				if(res.contains(i)) continue;
				result.add(i);
			}
			return result;
		};
	}
	
	public static <T> Query<T> eq(String index, Object value) {
		Holder holder = QueryUtils.holderOf(value);
		return (tbl, args)->QueryUtils.getValue(tbl.getIndex(index), holder.get(args));
	}
	
/*	public static <T> Query<T> empty(String index) {
		return (tbl, args)->QueryUtils.getValue(tbl.getIndex(index), EMPTY);
	}
	
	public static <T> Query<T> nul(String index) {
		return (tbl, args)->QueryUtils.getValue(tbl.getIndex(index), null);
	}*/
	
	public static <T> Query<T> between(String index, Object fromValue, Object toValue) {
		Holder fromHolder = QueryUtils.holderOf(fromValue);
		Holder toHolder = QueryUtils.holderOf(toValue);
		return (tbl, args)->QueryUtils.getRange(tbl.getIndex(index), fromHolder.get(args), toHolder.get(args), true);
	}
	
	public static <T> Query<T> lt(String index, Object value) {
		Holder holder = QueryUtils.holderOf(value);
		return (tbl, args)->QueryUtils.getRange(tbl.getIndex(index),  null, holder.get(args), false);
	}
	
	public static <T> Query<T> lte(String index, Object value) {
		Holder holder = QueryUtils.holderOf(value);
		return (tbl, args)->QueryUtils.getRange(tbl.getIndex(index),  null, holder.get(args), true);
	}
	
	public static <T> Query<T> gt(String index, Object value) {
		Holder holder = QueryUtils.holderOf(value);
		return (tbl, args)->QueryUtils.getRange(tbl.getIndex(index),  holder.get(args), null, false);
	}
	
	public static <T> Query<T> gte(String index, Object value) {
		Holder holder = QueryUtils.holderOf(value);
		return (tbl, args)->QueryUtils.getRange(tbl.getIndex(index),  holder.get(args), null, true);
	}
	
	public static <T> Query<T> in(String index, Object... values) {
		return in(index, Arrays.asList(values));
	}

	public static <T> Query<T> in(String index, List<Object> objects) {
		if(null == objects || objects.isEmpty()) return (tbl, args)->null;
		
		Stream<Holder> holders = objects.stream().map(it->QueryUtils.holderOf(it));
		return (tbl, args)->{
			return QueryUtils.findIn(index, tbl, holders.map(it->it.get(args)).collect(Collectors.toList()));
		};
	}
	
	public static <T> Query<T> startsWith(String index, String value) {
		Holder holder = QueryUtils.holderOf(value);
		
		return (tbl, args)->{
			Object fromValue = holder.get(args);
			Object toValue = (String)fromValue+Character.MAX_VALUE;
			return QueryUtils.getRange(tbl.getIndex(index), fromValue, toValue, true);
		};
	}
}
