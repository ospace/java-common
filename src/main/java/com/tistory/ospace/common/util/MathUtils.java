package com.tistory.ospace.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class MathUtils {
	public static final BigDecimal MINUS_ONE = new BigDecimal("-1");
	
	public static BigDecimal add(BigDecimal... vals) {
		return add(Arrays.asList(vals));
	}
	
	public static BigDecimal add(List<BigDecimal> vals) {
		if(null == DataUtils.findFirst(vals, it->null!=it)) return null;
		
		BigDecimal result = BigDecimal.ZERO;
		for(BigDecimal val : vals) {
			if(null == val) continue;
			result = result.add(val, MathContext.DECIMAL64);
		}
		return result;
	}
	
	public static BigDecimal floorHundred(BigDecimal val) {
		return null == val ? null : BigDecimal.valueOf(val.setScale(-2, BigDecimal.ROUND_DOWN).longValue());
	}
	
	public static BigDecimal ceilHundred(BigDecimal val) {
		return null == val ? null : BigDecimal.valueOf(val.setScale(-2, BigDecimal.ROUND_UP).longValue());
	}
	
	public static BigDecimal ceilDecimalPoint(BigDecimal val) {
		return null == val ? null : BigDecimal.valueOf(val.setScale(0, BigDecimal.ROUND_UP).longValue());
	}
	
	public static BigDecimal add(BigDecimal l, BigDecimal r) {
		return null == r ? l : (null == l ? r : l.add(r));
	}
	
	public static BigDecimal subtract(BigDecimal l, BigDecimal r) {
		return null == r ? l : (null == l ? BigDecimal.ZERO.subtract(r) : l.subtract(r));
	}
	
	public static BigDecimal multiply(BigDecimal l, BigDecimal r) {
		return null == r ? null : (null == l ? null : l.multiply(r));
	}
	
	public static BigDecimal addRate(BigDecimal l, BigDecimal r) {
		return add(l, calcRate(l,r));
	}
	
	private static BigDecimal HUNDRED = BigDecimal.valueOf(100.0);
	public static BigDecimal calcRate(BigDecimal val, BigDecimal rate) {
		return val.multiply(rate).divide(HUNDRED);
	}
	
	public static BigDecimal add(BigDecimal l, BigDecimal r, boolean isRate) {
		return isRate ? addRate(l,r) : add(l,r);
	}
	
	public static Integer min(Integer l, Integer r) {
		if (null == l) return r;
		if (null == r) return l;
		
		return Math.min(l, r);
	}
	
	public static <R, T> Integer sum(Collection<T> data, Function<T, Integer> action) {
		return DataUtils.reduce(data, (ret, it) -> {
			int val = action.apply(it);
			return null == ret ? val : ret + val;
		});
	}
	
	public static <R, T> Integer min(Collection<T> data, Function<T, Integer> action) {
		return DataUtils.reduce(data, (ret, it) -> {
			int val = action.apply(it);
			return null == ret ? val : Math.min(ret,  val);
		});
	}
	
	public static <R, T> Integer max(Collection<T> data, Function<T, Integer> action) {
		return DataUtils.reduce(data, (ret, it) -> {
			int val = action.apply(it);
			return null == ret ? val : Math.max(ret,  val);
		});
	}
}
