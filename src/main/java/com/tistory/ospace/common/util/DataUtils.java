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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataUtils {
	
	public static <P> boolean isEmpty(P[] obj) {
		return (null == obj || 0 == obj.length);
	}
	
	public static <P> boolean isEmpty(byte[] obj) {
		return (null == obj || 0 == obj.length);
	}
	
	public static <P> boolean isEmpty(Collection<P> obj) {
		return (null == obj || obj.isEmpty());
	}

	public static <K, V> boolean isEmpty(Map<K, V> obj) {
		return (null == obj || obj.isEmpty());
	}

	public static <P> void forEach(Collection<P> data, Consumer<P> action) {
		Objects.requireNonNull(action);
		
		if(isEmpty(data)) return;

		for(P it: data) action.accept(it);
	}

	public static <P> void forEach(P[] data, Consumer<P> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return;
		
		for(P it: data) action.accept(it);
	}
	
	public static <P> void forEach(P[][] data, Consumer<P[]> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return;
		
		for(P[] it: data) action.accept(it);
	}
	
	public static <P> void forEach(Enumeration<P> data,  Consumer<P> action) {
		Objects.requireNonNull(action);
		
		if (null == data) return;
		
		while(data.hasMoreElements()) {
			action.accept(data.nextElement());
		}
	}
	
	public static <P> void forEach(Collection<P> data, BiConsumer<P, Integer> action) {
		Objects.requireNonNull(action);
		
		if(isEmpty(data)) return;

		int i=0;
		for(P it: data) action.accept(it,i++);
	}
	
	public static <P> void forEach(P[] data, BiConsumer<P, Integer> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return;
		
		int i=0;
		for(P it: data) action.accept(it, i++);
	}
	
	public static <P> void forEach(P[][] data, BiConsumer<P[], Integer> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return;
		
		int i=0;
		for(P[] it: data) action.accept(it, i++);
	}
	
	public static <P> void forEach(Enumeration<P> data,  BiConsumer<P, Integer> action) {
		Objects.requireNonNull(action);
		
		if (null == data) return;
		
		int i=0;
		while(data.hasMoreElements()) {
			action.accept(data.nextElement(), i++);
		}
	}
	
	public static <P> void until(Collection<P> data, Predicate<P> action) {
		Objects.requireNonNull(action);
		
		if(isEmpty(data)) return;
		
		for(P it: data) if(action.test(it)) break;
	}
	
	public static <P> void until(P[] data, Predicate<P> action) {
		Objects.requireNonNull(action);
		
		if(isEmpty(data)) return;
		
		for(P it: data) if(action.test(it)) break;
	}
	
	public static <P> void until(Enumeration<P> data, Predicate<P> action) {
		Objects.requireNonNull(action);
		
		if (null == data) return;
		
		while(data.hasMoreElements()) {
			P it = data.nextElement();
			if(action.test(it)) break;
		}
	}
	
	public static <P> P findFirst(Collection<P> data, Predicate<P> filter) {
		Objects.requireNonNull(filter);
		
		if (isEmpty(data)) return null;
		
		for(P it: data) {
			if(filter.test(it)) return it;
		}
		
		return null;
	}

	public static <P> P findFirst(P[] data, Predicate<P> filter) {
		Objects.requireNonNull(filter);
		
		if (isEmpty(data)) return null;
		
		for(P it: data) {
			if(filter.test(it)) return it;
		}
		
		return null;
	}
	
	public static <P> P findFirst(Enumeration<P> data, Predicate<P> filter) {
		Objects.requireNonNull(filter);
		
		if (null == data) return null;
		
		while(data.hasMoreElements()) {
			P it = data.nextElement();
			if(filter.test(it)) return it;
		}
		
		return null;
	}

	//필터 조건에 맞는 대상은 제거됨
	public static <P> List<P> filter(Collection<P> data, Predicate<P> filter) {
		Objects.requireNonNull(filter);
		
		if (isEmpty(data)) return null;
		
		List<P> ret = new ArrayList<>();
		for(P it: data) if(!filter.test(it)) ret.add(it);
		
		return ret;
	}

	//필터 조건에 맞는 대상은 제거됨
	public static <P> List<P> filter(P[] data, Predicate<P> filter) {
		Objects.requireNonNull(filter);
		
		if (isEmpty(data)) return null;
		
		List<P> ret = new ArrayList<>();
		for(P it: data) if(!filter.test(it)) ret.add(it);
		
		return ret;
	}
	
	public static <P> List<P> filter(Enumeration<P> data, Predicate<P> filter) {
		Objects.requireNonNull(filter);
		
		if (null == data) return null;
		
		List<P> ret = new ArrayList<>();
		while(data.hasMoreElements()) {
			P it = data.nextElement();
			if(!filter.test(it)) ret.add(it);
		}
		
		return ret;
	}

	public static <R, P> List<R> map(Collection<P> data, Function<P, R> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return null;
		
		List<R> ret = new ArrayList<>();
		for(P it: data) ret.add(action.apply(it));
		
		return ret;
	}

	public static <R, P> List<R> map(P[] data, Function<P, R> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return null;
		
		List<R> ret = new ArrayList<>();
		for(P it: data) ret.add(action.apply(it));
		
		return ret;
	}
	
	public static <R, P> List<R> map(Enumeration<P> data, Function<P, R> action) {
		Objects.requireNonNull(action);
		
		if (null == data) return null;
		
		List<R> ret = new ArrayList<>();
		while(data.hasMoreElements()) {
			ret.add(action.apply(data.nextElement()));
		}
		
		return ret;
	}
	
	public static <K, V, P> Map<K,V> map(Collection<P> data, Function<P, K> key, Function<P, V> value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		
		if (isEmpty(data)) return null;
		
		Map<K,V> ret = new HashMap<>();
		for(P it: data) ret.put(key.apply(it), value.apply(it));
		
		return ret;
	}

	public static <K, V, P> Map<K,V> map(P[] data, Function<P, K> key, Function<P, V> value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		
		if (isEmpty(data)) return null;
		
		Map<K,V> ret = new HashMap<>();
		for(P it: data) ret.put(key.apply(it), value.apply(it));
		
		return ret;
	}
	
	public static <K, V, P> Map<K,V> map(Enumeration<P> data, Function<P, K> key, Function<P, V> value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		
		if (null == data) return null;
		
		Map<K,V> ret = new HashMap<>();
		while(data.hasMoreElements()) {
			P it = data.nextElement();
			ret.put(key.apply(it), value.apply(it));
		}
		
		return ret;
	}
	
	public static <R, P> List<R> map(Collection<P> data, Function<P, R> action, ExecutorService executor) {
		Objects.requireNonNull(action);
		
		return map(data, action, null, executor);
	}
	
	public static <R, P> List<R> map(Collection<P> data, Function<P, R> action, BiFunction<P, Throwable, R> except, ExecutorService executor) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return null;
		
		return allOf(map(data, it->createFuture(it, action, except, executor)));
	}
	
	public static <R, P> List<R> map(P[] data, Function<P, R> action, ExecutorService executor) {
		Objects.requireNonNull(action);
		
		return map(data, action, null, executor);
	}
	
	public static <R, P> List<R> map(P[] data, Function<P, R> action, BiFunction<P, Throwable, R> except, ExecutorService executor) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return null;
		
		return allOf(map(data, it->createFuture(it, action, except, executor)));
	}
	
	public static <R, P> List<R> map(Enumeration<P> data, Function<P, R> action, ExecutorService executor) {
		Objects.requireNonNull(action);
		
		return map(data, action, null, executor);
	}
	
	public static <R, P> List<R> map(Enumeration<P> data, Function<P, R> action, BiFunction<P, Throwable, R> except, ExecutorService executor) {
		Objects.requireNonNull(action);
		
		if (null == data) return null;
		
		return allOf(map(data, it->createFuture(it, action, except, executor)));
	}
	
	public static <P,R> CompletableFuture<R> createFuture(P item, Function<P, R> action, BiFunction<P, Throwable, R> except, ExecutorService executor) {
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
	
	public static <K, P> Map<K, List<P>> partitioning(Collection<P> data, Function<P, K> key) {
		Objects.requireNonNull(key);
		
		if (isEmpty(data)) return null;
		
		Map<K, List<P>> ret = new HashMap<>();
		for(P it: data) multimapAdd(ret, key.apply(it), it);
		
		return ret;
	}
	
	public static <K, P> Map<K, List<P>> partitioning(P[] data, Function<P, K> key) {
		Objects.requireNonNull(key);
		
		if (isEmpty(data)) return null;
		
		Map<K, List<P>> ret = new HashMap<>();
		for(P it: data) multimapAdd(ret, key.apply(it), it);
		
		return ret;
	}
	
	public static <K, P> Map<K, List<P>> partitioning(Enumeration<P> data, Function<P, K> key) {
		Objects.requireNonNull(key);
		
		if (null == data) return null;
		
		Map<K, List<P>> ret = new HashMap<>();
		while(data.hasMoreElements()) {
			P it = data.nextElement();
			multimapAdd(ret, key.apply(it), it);
		}
		
		
		return ret;
	}
	
	public static <K,P> void multimapAdd(Map<K, List<P>> data, K key, P value) {
		List<P> found = data.get(key);
		if(null == found) {
			data.put(key, found = new ArrayList<>());
		}
		found.add(value);
	}


	public static <R, P> R reduce(Collection<P> data, BiConsumer<R, P> action, R init) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return init;
		
		for(P it: data) action.accept(init, it);
		return init;
		
	}
	
	public static <R, P> R reduce(P[] data, BiConsumer<R, P> action, R init) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return init;
		
		for(P it: data) action.accept(init, it);
		
		return init;
	}
	
	public static <R, P> R reduce(Enumeration<P> data, BiConsumer<R, P> action, R init) {
		Objects.requireNonNull(action);
		
		if (null == data) return init;
		
		while(data.hasMoreElements()) {
			action.accept(init, data.nextElement());
		}
		
		return init;
	}
	

	public static <R, P> R reduce(Collection<P> data, BiFunction<R, P, R> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return null;
		
		R ret = null;
		for(P it: data) ret = action.apply(ret, it);
		
		return ret;
	}
	
	public static <R, P> R reduce(P[] data, BiFunction<R, P, R> action) {
		Objects.requireNonNull(action);
		
		if (isEmpty(data)) return null;
		
		R ret = null;
		for(P it: data) ret = action.apply(ret, it);
		
		return ret;
	}
	
	public static <R, P> R reduce(Enumeration<P> data, BiFunction<R, P, R> action) {
		Objects.requireNonNull(action);
		
		if (null == data) return null;
		
		R ret = null;
		while(data.hasMoreElements()) {
			ret = action.apply(ret, data.nextElement());
		}
		
		return ret;
	}

	public static <P> List<List<P>> combination(List<List<P>> dataSet) {
		return reduce(dataSet, DataUtils::combination);
	}

	public static <P> List<List<P>> combination(List<List<P>> ret, Collection<P> values) {
		if(isEmpty(ret)) {
			return reduce(values, (r,v)->r.add(new ArrayList<>(Arrays.asList(v))), new ArrayList<>());
		}
		
		return reduce(ret, (r,d)->forEach(values, v->r.add(add(d, v))), new ArrayList<>());
	}

	public static <P> List<List<P>> transform(List<List<P>> dataSet) {
		int max_size = maxSize(dataSet);
		List<List<P>> ret = new ArrayList<>();
		for(int i=0; i<max_size; ++i) ret.add(new ArrayList<>());
		
		for(Collection<P> it : dataSet) {
			zip(ret, it, (r,v)->r.add(v));
		}
		
		return ret;
	}

	public static <P> List<P> add(Collection<P> l, P r) {
		List<P> ret = new ArrayList<>(l);
		ret.add(r);
		
		return ret;
	}

	public static <P> List<P> add(List<P> l, List<P> r) {
		if (isEmpty(l)) return r;
		if (isEmpty(r)) return l;
		
		List<P> ret = new ArrayList<>(l);
		ret.addAll(r);
		
		return ret;
	}

	public static <P, U> void zip(Collection<P> data1, Collection<U> data2, BiConsumer<P, U> action) {
		if (isEmpty(data1) || isEmpty(data2)) return;
		Iterator<P> it1 = data1.iterator();
		Iterator<U> it2 = data2.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			action.accept(it1.next(), it2.next());
		}
	}
	
	public static <P> int maxSize(List<List<P>> list) {
		if (isEmpty(list)) return 0;
		int max_size = 0;
		for(Collection<P> it : list) {
			if(null == it) continue;
			max_size = Math.max(max_size, it.size());
		}
		
		return max_size;
	}
	
	public static <P> int maxSize2(List<List<P>> list) {
		return reduce(list, (ret, it)->{
			if (null == ret) return it.size();
			return ret < it.size() ? it.size() : ret; 
		});
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
	
	public static <P> boolean contains(P[] data, P val) {
		if (isEmpty(data)) return false;
		
		for(P it : data) {
			if (it.equals(val)) return true;
		}
		
		return false;
	}
	
	public static <P> boolean contains(Collection<P> data, P val) {
		if (isEmpty(data)) return false;
		
		return data.contains(val);
	}
	
	public static <P> boolean contains(Enumeration<P> data, P val) {
		if( null == data || !data.hasMoreElements()) return false;
		
		while(data.hasMoreElements()) {
			if (data.nextElement().equals(val)) return true;
		}
		
		return false;
	}

}