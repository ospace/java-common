package com.tistory.ospace.common.indexedtable.query;

import com.tistory.ospace.common.indexedtable.ResultSet;

public interface Queryable<T> {
	public abstract ResultSet<T> query(final Query<T> query);
}
