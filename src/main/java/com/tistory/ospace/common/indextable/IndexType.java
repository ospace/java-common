package com.tistory.ospace.common.indextable;

import com.tistory.ospace.common.util.DataUtils;

public enum IndexType {
	HASH("Hash"),
	TREE("Tree"),
	LINKED("Linked");
	
	public final String code;
	
	private IndexType(String code) {
		this.code = code;
	}
	
	public boolean equals(String code) {
		return this.code.equals(code);
	}
	
	public static IndexType getCode(String code) {
		return DataUtils.findFirst(IndexType.values(), it->it.equals(code));
	}
}
