package com.tistory.ospace.common.indexedtable.query;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import com.tistory.ospace.common.indexedtable.Table;


public interface Query<T> extends BiFunction<Table<T>, List<Object>, Collection<Integer>>{
}
