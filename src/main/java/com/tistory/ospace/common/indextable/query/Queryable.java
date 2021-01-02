package com.tistory.ospace.common.indextable.query;

import com.tistory.ospace.common.indextable.core.ResultSet;

public interface Queryable<T> {
	public abstract ResultSet<T> query(final Query<T> query);
}
