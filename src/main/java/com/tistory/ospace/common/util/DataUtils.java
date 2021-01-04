package com.tistory.ospace.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
	public static <T> void until(Collection<T> data, Predicate<T> action) {
		if(isEmpty(data)) return;
		
		for(T it: data) if(action.test(it)) break;
	}
	
	public static <T> void until(T[] data, Predicate<T> action) {
		if(isEmpty(data)) return;
		
		for(T it: data) if(action.test(it)) break;
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
		
		T tmpIt = null;
		for(T it: data) {
			if(filter.test(it)) {
				tmpIt =  it; break;
			}
		}
		
		return tmpIt;
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
	
	public <T, R> List<R> map(Collection<T> data, Function<T, R> action, ExecutorService executor) {
		return map(data, action, null, executor);
	}
	
	public <T, R> List<R> map(Collection<T> data, Function<T, R> action, BiFunction<T, Throwable, R> except, ExecutorService executor) {
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
	
	public static <K,T> void multimapAdd(Map<K, List<T>> data, K key, T value) {
		List<T> found = data.get(key);
		if(null == found) {
			data.put(key, found = new ArrayList<>());
		}
		found.add(value);
	}

	public static <R, T> R reduce(T[] data, BiConsumer<R,T> action, R init) {
		if (isEmpty(data)) return init;
		
		for(T it: data) action.accept(init, it);
		
		return init;
	}

	public static <R, T> R reduce(Collection<T> data, BiConsumer<R,T> action, R init) {
		if (isEmpty(data)) return init;
		
		for(T it: data) action.accept(init, it);
		return init;
		
	}

	public static <R, T> R reduce(T[] data, BiFunction<R,T,R> action) {
		if (isEmpty(data)) return null;
		R ret = null;
		for(T it: data) ret = action.apply(ret, it);
		return ret;
	}

	public static <R, T> R reduce(Collection<T> data, BiFunction<R,T,R> action) {
		if (isEmpty(data)) return null;
		
		R ret = null;
		for(T it: data) ret = action.apply(ret, it);
		
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
	
	public static <T> int size(Collection<T> list) {
		return isEmpty(list) ? 0 : list.size();
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
	
	public static <P> P[] toArray(List<P> data) {
		if(DataUtils.isEmpty(data)) return null;
		
		@SuppressWarnings("unchecked")
		P[] ret = (P[]) Array.newInstance(data.get(0).getClass(), data.size());
		data.toArray(ret);
		
		return ret;
	}
	
	public static Integer min(Integer l, Integer r) {
		if (null == l) return r;
		if (null == r) return l;
		
		return Math.min(l, r);
	}
	
	final static int prime = 31;
	public static int hashCode(Object ...args) {
		if (DataUtils.isEmpty(args)) return 0;
		
		int result = 1;
		for(Object each : args) {
			result = prime * result + (null == each ? 0 : each.hashCode());
		}
		
		return result;
	}
	
	public static <R, T> Integer sum(Collection<T> data, Function<T, Integer> action) {
		return reduce(data, (ret, it) -> {
			int val = action.apply(it);
			return null == ret ? val : ret + val;
		});
	}
	
	public static <R, T> Integer min(Collection<T> data, Function<T, Integer> action) {
		return reduce(data, (ret, it) -> {
			int val = action.apply(it);
			return null == ret ? val : Math.min(ret,  val);
		});
	}
	
	public static <R, T> Integer max(Collection<T> data, Function<T, Integer> action) {
		return reduce(data, (ret, it) -> {
			int val = action.apply(it);
			return null == ret ? val : Math.max(ret,  val);
		});
	}

	@SafeVarargs
	public static <T> List<T> asList(T ...args) {
		if(DataUtils.isEmpty(args)) return null;
		if(1 == args.length && null == args[0]) return null;
		
		return Arrays.asList(args);
	}

	public static <P extends Comparable<P>> List<P> toSortedList(Set<P> data) {
		List<P> ret = new ArrayList<>(data);
		ret.sort((l,r)->l.compareTo(r));
		return ret;
	}
}