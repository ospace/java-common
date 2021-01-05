package com.tistory.ospace.common.indexedtable;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class ResultSet<T> implements Iterable<T> {
	private Collection<Integer> result;
	private Table<T>            table;
	
	public ResultSet(Collection<Integer> result, Table<T> table) {
		this.result = result;
		this.table = table;
	}
	
	public Collection<Integer> getIDs() {
		return this.result;
	}
	
	@Override
	public Iterator<T> iterator() {
		if (null == result) {
			return new Iterator<T>() {
				@Override
				public boolean hasNext() { return  false; }
				
				@Override
				public T next() { return null; }
			};
		}
		
		return new Iterator<T>() {
			Iterator<Integer> it = result.iterator();
			
			@Override
			public boolean hasNext() { return it.hasNext(); }

			@Override
			public T next() { return table.get(it.next()); }
		};
	}
	
	public int size() {
		return null == result ? 0 : result.size();
	}
	
	public boolean isEmpty() {
		return null == result ? true : result.isEmpty();
	}
	
/*	public void forEach(Consumer<T> consumer) {
		if(null == result) return;
		for(Integer it : result) consumer.accept(table.getData(it));
	}*/
	
	public Collection<T> values() {
		if(null == result || result.isEmpty()) return null;
		return result.stream().map(it->table.get(it)).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		Iterator<T> it = iterator();
		if (it.hasNext()) {
			sb.append(it.next());
			while(it.hasNext()) {
				sb.append(",").append(it.next());
			}
		}
		sb.append("]");
		
		return sb.toString();
	}

}
