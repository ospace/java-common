package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.ospace.common.util.DataUtils;

public class TestDataUtils {
	@Test
	public void testforEach() {
		String weeks[] = {"one", "two", "three"};
		String expected1 = "onetwothree";
		String expected2 = "one0two1three2";
		
		StringBuilder sb = new StringBuilder();
		DataUtils.forEach(weeks, it->sb.append(it));
		Assert.assertTrue(expected1.equals(sb.toString()));
		
		sb.setLength(0);
		DataUtils.forEach(weeks, (it,idx)->sb.append(it).append(idx));
		Assert.assertTrue(expected2.equals(sb.toString()));
		
		Vector<String> data = new Vector<>();
		data.add("one");
		data.add("two");
		data.add("three");

		sb.setLength(0);
		DataUtils.forEach(data, it->sb.append(it));
		Assert.assertTrue(expected1.equals(sb.toString()));
		
		sb.setLength(0);
		DataUtils.forEach(data, (it,idx)->sb.append(it).append(idx));
		Assert.assertTrue(expected2.equals(sb.toString()));
	}
	
	@Test
	public void testMap() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		List<String> res = DataUtils.map(foos, it->it.getName());
		
		Assert.assertEquals(3, res.size());
		Assert.assertEquals("one", res.get(0));
		Assert.assertEquals("two", res.get(1));
		Assert.assertEquals("three", res.get(2));
	}
	
	@Test
	public void testReduce() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		Integer res = DataUtils.reduce(foos, (r,it)->(null==r?it.getNum():r+it.getNum()));
		
		Assert.assertEquals(6, (int)res);
	}
	
	@Test
	public void testReduce2() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		StringBuffer res = DataUtils.reduce(foos, (r,it)->r.append(it.getName()).append(","), new StringBuffer());
		
		Assert.assertEquals("one,two,three,", res.toString());
	}
	
	@Test
	public void testFilter() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		List<Foo> res = DataUtils.filter(foos, it->0==it.getNum()%2);
		
		Assert.assertEquals(2, res.size());
		Assert.assertEquals("one", res.get(0).getName());
		Assert.assertEquals("three", res.get(1).getName());
	}

	@Test
	public void testFindFirst() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		Foo res = DataUtils.findFirst(foos, it->2==it.getNum());
		
		Assert.assertEquals(2, (int)res.getNum());
	}
	
	@Test
	public void testZip() {
		List<Foo> foos1 = Arrays.asList(new Foo("ten", 10), new Foo("twenty", 20), new Foo("thirty", 30));
		List<Foo> foos2 = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3));
		
		List<String> res = new ArrayList<>();
		DataUtils.zip(foos1, foos2, (l,r)->res.add(l.getName()+"+"+r.getName()+"="+(l.getNum()+r.getNum())));
		
		String[] expected = {"ten+one=11", "twenty+two=22", "thirty+three=33"};
		Assert.assertArrayEquals(expected, res.toArray());
	}
	
	@Test
	public void testTransform() {
		List<List<Integer>> nums = Arrays.asList(
				Arrays.asList(01,02,03),
				Arrays.asList(11,12,13),
				Arrays.asList(21,22,23));
		
		nums = DataUtils.transform(nums);
		System.out.println(nums);
	}
	
	@Test
	public void testCombination() {
		List<List<Integer>> nums = Arrays.asList(
				Arrays.asList(01,02,03),
				Arrays.asList(11,12,13));
		nums = DataUtils.combination(nums);
		System.out.println(nums);

	}
	
	@Test
	public void testMap2() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		Map<Integer,String> res = DataUtils.map(foos, it->it.getNum(), it->it.getName());
		System.out.println(res);
	}
	
	@Test
	public void testPartitioning() {
		List<Foo> foos = Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)); 
		Map<Boolean, List<Foo>> res = DataUtils.partitioning(foos, it->0==it.getNum()%2);
		System.out.println(res);
		System.out.println(res.get(false));

	}
	
	@Test
	public void testSort() {
		String weeks[] = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
		List<String> data = Arrays.asList(weeks);
		//data.sort(compareTo(it->it));
		data.sort((l,r)->l.compareTo(r));
	}
}
