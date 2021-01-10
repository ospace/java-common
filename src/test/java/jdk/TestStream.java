package jdk;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class TestStream {
	@Test
	public void testStream1() {
		long num = IntStream.iterate(0, i->i+1).limit(11).mapToLong(i->i).sum();
		
		Assert.assertEquals(55, num);
		
		List<Integer> nums = new ArrayList<>();
		IntStream.rangeClosed(1,10).forEach(i->nums.add(i));
		int val = nums.stream().mapToInt(i->i).sum();
		
		Assert.assertEquals(55, val);
		
		List<Integer> nums2 = nums.stream().map(i->i*10).collect(Collectors.toList());
		
		Assert.assertEquals(10, nums2.size());
		Assert.assertEquals(10, (int)nums2.get(0));
		Assert.assertEquals(100, (int)nums2.get(9));
	}
	
	@Test
	public void testStream2() {
		List<Integer> nums = IntStream.rangeClosed(1,10).boxed().collect(Collectors.toList());
		Integer val = nums.stream().reduce((r,i)->r+i).get();
		
		Assert.assertEquals((Integer)55, val);
	}
}
