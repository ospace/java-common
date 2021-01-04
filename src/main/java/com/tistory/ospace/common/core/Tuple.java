package com.tistory.ospace.common.core;

import com.tistory.ospace.common.util.CmmUtils;

public class Tuple<L,R> extends BaseObject {
	public final L left;
	public final R right;
	
	public static <T,U> Tuple<T,U> of(T left, U right) {
		return new Tuple<>(left, right);
	}
	
	public Tuple(L left, R right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null) return false;
		if (!(other instanceof Tuple)) return false;
		
		return ((Tuple<?,?>) other).left.equals(left)
		        && ((Tuple<?,?>) other).right.equals(right);
	}
	
	public int hashCode() {
		return CmmUtils.hashCode(left, right);
	}
}