package com.tistory.ospace.common.util;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataUtils {
	
	public static <T> boolean isEmpty(Collection<T> obj) {
		return (null == obj || obj.isEmpty());
	}

	public static <K, V> boolean isEmpty(Map<K, V> obj) {
		return (null == obj || obj.isEmpty());
	}

	public static <T> boolean isEmpty(T[] obj) {
		return (null == obj || 0 == obj.length);
	}
	
	public static <T> void iterate(Collection<T> data, Consumer<T> action) {
		if(isEmpty(data)) return;
		
		for(T it: data) action.accept(it);
	}

	public static <T> void iterate(T[] data, Consumer<T> action) {
		if (isEmpty(data)) return;
		
		for(T it: data) action.accept(it);
	}
	
	public static <T> void iterate(T[][] data, Consumer<T[]> action) {
		if (isEmpty(data)) return;
		
		for(T[] it: data) action.accept(it);
	}
	
	public static <T> void iterate(Enumeration<T> data,  Consumer<T> action) {
		if (null == data) return;
		
		while(data.hasMoreElements()) {
			action.accept(data.nextElement());
		}
	}
	
	public static <T> void until(Collection<T> data, Predicate<T> action) {
		if(isEmpty(data)) return;
		
		for(T it: data) if(action.test(it)) break;
	}
	
	public static <T> void until(T[] data, Predicate<T> action) {
		if(isEmpty(data)) return;
		
		for(T it: data) if(action.test(it)) break;
	}
	
	public static <T> void until(Enumeration<T> data, Predicate<T> action) {
		if (null == data) return;
		
		while(data.hasMoreElements()) {
			T it = data.nextElement();
			if(action.test(it)) break;
		}
	}
	
	public static <T> T findFirst(Collection<T> data, Predicate<T> filter) {
		if (isEmpty(data)) return null;
		
		for(T it: data) {
			if(filter.test(it)) return it;
		}
		
		return null;
	}

	public static <T> T findFirst(T[] data, Predicate<T> filter) {
		if (isEmpty(data)) return null;
		
		for(T it: data) {
			if(filter.test(it)) return it;
		}
		
		return null;
	}
	
	public static <T> T findFirst(Enumeration<T> data, Predicate<T> filter) {
		if (null == data) return null;
		
		while(data.hasMoreElements()) {
			T it = data.nextElement();
			if(filter.test(it)) return it;
		}
		
		return null;
	}

	//필터 조건에 맞는 대상은 제거됨
	public static <T> List<T> filter(Collection<T> data, Predicate<T> filter) {
		if (isEmpty(data)) return null;
		
		List<T> ret = new ArrayList<>();
		for(T it: data) if(!filter.test(it)) ret.add(it);
		
		return ret;
	}

	//필터 조건에 맞는 대상은 제거됨
	public static <T> List<T> filter(T[] data, Predicate<T> filter) {
		if (isEmpty(data)) return null;
		
		List<T> ret = new ArrayList<>();
		for(T it: data) if(!filter.test(it)) ret.add(it);
		
		return ret;
	}
	
	public static <T> List<T> filter(Enumeration<T> data, Predicate<T> filter) {
		if (null == data) return null;
		
		List<T> ret = new ArrayList<>();
		while(data.hasMoreElements()) {
			T it = data.nextElement();
			if(!filter.test(it)) ret.add(it);
		}
		
		return ret;
	}

	public static <R, T> List<R> map(Collection<T> data, Function<T, R> action) {
		if (isEmpty(data)) return null;
		
		List<R> ret = new ArrayList<>();
		for(T it: data) ret.add(action.apply(it));
		
		return ret;
	}

	public static <R, T> List<R> map(T[] data, Function<T, R> action) {
		if (isEmpty(data)) return null;
		
		List<R> ret = new ArrayList<>();
		for(T it: data) ret.add(action.apply(it));
		
		return ret;
	}
	
	public static <R, T> List<R> map(Enumeration<T> data, Function<T, R> action) {
		if (null == data) return null;
		
		List<R> ret = new ArrayList<>();
		while(data.hasMoreElements()) {
			ret.add(action.apply(data.nextElement()));
		}
		
		return ret;
	}
	
	public static <K, V, T> Map<K,V> map(Collection<T> data, Function<T, K> key, Function<T, V> value) {
		if (isEmpty(data)) return null;
		
		Map<K,V> ret = new HashMap<>();
		for(T it: data) ret.put(key.apply(it), value.apply(it));
		
		return ret;
	}

	public static <K, V, T> Map<K,V> map(T[] data, Function<T, K> key, Function<T, V> value) {
		if (isEmpty(data)) return null;
		
		Map<K,V> ret = new HashMap<>();
		for(T it: data) ret.put(key.apply(it), value.apply(it));
		
		return ret;
	}
	
	public static <K, V, T> Map<K,V> map(Enumeration<T> data, Function<T, K> key, Function<T, V> value) {
		if (null == data) return null;
		
		Map<K,V> ret = new HashMap<>();
		while(data.hasMoreElements()) {
			T it = data.nextElement();
			ret.put(key.apply(it), value.apply(it));
		}
		
		return ret;
	}
	
	public static <T, R> List<R> map(Collection<T> data, Function<T, R> action, ExecutorService executor) {
		return map(data, action, null, executor);
	}
	
	public static <T, R> List<R> map(Collection<T> data, Function<T, R> action, BiFunction<T, Throwable, R> except, ExecutorService executor) {
		if (isEmpty(data)) return null;
		
		return allOf(map(data, it->createFuture(it, action, except, executor)));
	}
	
	public static <T, R> List<R> map(T[] data, Function<T, R> action, ExecutorService executor) {
		return map(data, action, null, executor);
	}
	
	public static <T, R> List<R> map(T[] data, Function<T, R> action, BiFunction<T, Throwable, R> except, ExecutorService executor) {
		if (isEmpty(data)) return null;
		
		return allOf(map(data, it->createFuture(it, action, except, executor)));
	}
	
	public static <T, R> List<R> map(Enumeration<T> data, Function<T, R> action, ExecutorService executor) {
		return map(data, action, null, executor);
	}
	
	public static <T, R> List<R> map(Enumeration<T> data, Function<T, R> action, BiFunction<T, Throwable, R> except, ExecutorService executor) {
		if (null == data) return null;
		
		return allOf(map(data, it->createFuture(it, action, except, executor)));
	}
	
	public static <T,R> CompletableFuture<R> createFuture(T item, Function<T, R> action, BiFunction<T, Throwable, R> except, ExecutorService executor) {
		// RequestAttributes atts = RequestContextHolder.getRequestAttributes();
		return CompletableFuture.supplyAsync(()->{
			// RequestContextHolder.setRequestAttributes(atts);
			return action.apply(item);
		}, executor).exceptionally(e->{
			return null == except ? null : except.apply(item, e);
 	    });
	}
	
	public static <R> List<R> allOf(List<CompletableFuture<R>> futures) {
		List<R> result = new ArrayList<>();
		for(CompletableFuture<R> it: futures) {
			it.thenAccept(res->result.add(res));
		}
		
		try {
			CompletableFuture.allOf(toArray(futures)).get();
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	public static <K, T> Map<K, List<T>> partitioning(Collection<T> data, Function<T, K> key) {
		if (isEmpty(data)) return null;
		
		Map<K, List<T>> ret = new HashMap<>();
		for(T it: data) multimapAdd(ret, key.apply(it), it);
		
		return ret;
	}
	
	public static <K, T> Map<K, List<T>> partitioning(T[] data, Function<T, K> key) {
		if (isEmpty(data)) return null;
		
		Map<K, List<T>> ret = new HashMap<>();
		for(T it: data) multimapAdd(ret, key.apply(it), it);
		
		return ret;
	}
	
	public static <K, T> Map<K, List<T>> partitioning(Enumeration<T> data, Function<T, K> key) {
		if (null == data) return null;
		
		Map<K, List<T>> ret = new HashMap<>();
		while(data.hasMoreElements()) {
			T it = data.nextElement();
			multimapAdd(ret, key.apply(it), it);
		}
		
		
		return ret;
	}
	
	public static <K,T> void multimapAdd(Map<K, List<T>> data, K key, T value) {
		List<T> found = data.get(key);
		if(null == found) {
			data.put(key, found = new ArrayList<>());
		}
		found.add(value);
	}


	public static <R, T> R reduce(Collection<T> data, BiConsumer<R,T> action, R init) {
		if (isEmpty(data)) return init;
		
		for(T it: data) action.accept(init, it);
		return init;
		
	}
	
	public static <R, T> R reduce(T[] data, BiConsumer<R,T> action, R init) {
		if (isEmpty(data)) return init;
		
		for(T it: data) action.accept(init, it);
		
		return init;
	}
	
	public static <R, T> R reduce(Enumeration<T> data, BiConsumer<R,T> action, R init) {
		if (null == data) return init;
		
		while(data.hasMoreElements()) {
			action.accept(init, data.nextElement());
		}
		
		return init;
	}
	

	public static <R, T> R reduce(Collection<T> data, BiFunction<R,T,R> action) {
		if (isEmpty(data)) return null;
		
		R ret = null;
		for(T it: data) ret = action.apply(ret, it);
		
		return ret;
	}
	
	public static <R, T> R reduce(T[] data, BiFunction<R,T,R> action) {
		if (isEmpty(data)) return null;
		
		R ret = null;
		for(T it: data) ret = action.apply(ret, it);
		
		return ret;
	}
	
	public static <R, T> R reduce(Enumeration<T> data, BiFunction<R,T,R> action) {
		if (null == data) return null;
		
		R ret = null;
		while(data.hasMoreElements()) {
			ret = action.apply(ret, data.nextElement());
		}
		
		return ret;
	}

	public static <T> Collection<Collection<T>> combination(Collection<Collection<T>> dataSet) {
		return reduce(dataSet, DataUtils::combination);
	}

	public static <T> Collection<Collection<T>> combination(Collection<Collection<T>> ret, Collection<T> values) {
		if(isEmpty(ret)) {
			return reduce(values, (r,v)->r.add(new ArrayList<>(Arrays.asList(v))), new ArrayList<>());
		}
		
		return reduce(ret, (r,d)->iterate(values, v->r.add(add(d, v))), new ArrayList<>());
	}

	public static <T> List<List<T>> transform(Collection<Collection<T>> dataSet) {
		int max_size = maxSize(dataSet);
		List<List<T>> ret = new ArrayList<>();
		for(int i=0; i<max_size; ++i) ret.add(new ArrayList<>());
		
		for(Collection<T> it : dataSet) {
			zip(ret, it, (r,v)->r.add(v));
		}
		
		return ret;
	}

	public static <T> List<T> add(Collection<T> l, T r) {
		List<T> ret = new ArrayList<>(l);
		ret.add(r);
		
		return ret;
	}

	public static <T> List<T> add(List<T> l, List<T> r) {
		if (isEmpty(l)) return r;
		if (isEmpty(r)) return l;
		
		List<T> ret = new ArrayList<>(l);
		ret.addAll(r);
		
		return ret;
	}

	public static <T,U> void zip(Collection<T> data1, Collection<U> data2, BiConsumer<T,U> action) {
		if (isEmpty(data1) || isEmpty(data2)) return;
		Iterator<T> it1 = data1.iterator();
		Iterator<U> it2 = data2.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			action.accept(it1.next(), it2.next());
		}
	}
	
	public static <T> int maxSize(Collection<Collection<T>> list) {
		if (isEmpty(list)) return 0;
		int max_size = 0;
		for(Collection<T> it : list) {
			if(null == it) continue;
			max_size = Math.max(max_size, it.size());
		}
		
		return max_size;
	}
	
	public static <P> P[] toArray(Collection<P> data) {
		if(DataUtils.isEmpty(data)) return null;
		
		return toArray(data, data.iterator().next().getClass());
	}
	
	public static <P> P[] toArray(Collection<P> data, Class<? extends Object> clazz) {
		if(DataUtils.isEmpty(data)) return null;
		
		@SuppressWarnings("unchecked")
		P[] ret = (P[]) Array.newInstance(clazz, data.size());
		data.toArray(ret);
		
		return ret;
	}
	
	public static List<LocalDate> range(LocalDate first, LocalDate last) {
		List<LocalDate> ret = new ArrayList<>();
		
		for(LocalDate it = first; it.isBefore(last); it=it.plusDays(1)) {
		    ret.add(it);
		}
		
		return ret;
	}
}