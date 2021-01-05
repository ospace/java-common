package com.tistory.ospace.common.indexedtable.query;

import java.util.List;

public class HolderArgument implements Holder {
	private int idx;

	public HolderArgument(int idx) {
		this.idx = idx;
	}
	
	@Override
	public Object get(List<Object> args) {
		if(null == args || args.size() < idx) {
			throw new RuntimeException("Over range of holder or Non-arguments");
		}
		return args.get(idx);
	}
}
