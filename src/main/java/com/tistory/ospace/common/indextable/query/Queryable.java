package com.tistory.ospace.common.indextable.query;

import com.tistory.ospace.common.indextable.ResultSet;

public interface Queryable<T> {
	public abstract ResultSet<T> query(final Query<T> query);
}
