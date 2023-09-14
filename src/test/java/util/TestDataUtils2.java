package util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.ospace.common.util.DataUtils;

public class TestDataUtils2 {
	@Test
	public void testforEach() {
		String rawData[] = {"one", "two", "three"};
		String expected1 = "onetwothree";
		String expected2 = "one0two1three2";
		
		DataUtils<?> weeks = DataUtils.of(rawData);
		
		StringBuilder sb = new StringBuilder();
		weeks.forEach(it->sb.append(it));
//		DataUtils.forEach(weeks, it->sb.append(it));
		Assert.assertTrue(expected1.equals(sb.toString()));
		
		sb.setLength(0);
		weeks.forEach((it,idx)->sb.append(it).append(idx));
		Assert.assertTrue(expected2.equals(sb.toString()));
		
		Vector<String> data = new Vector<>();
		data.add("one");
		data.add("two");
		data.add("three");

		sb.setLength(0);
		weeks.forEach(it->sb.append(it));
		Assert.assertTrue(expected1.equals(sb.toString()));
		
		sb.setLength(0);
		weeks.forEach((it,idx)->sb.append(it).append(idx));
		Assert.assertTrue(expected2.equals(sb.toString()));
	}
	
	@Test
	public void testMap() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		List<String> res = foos.map(it->it.getName()).toList();
		
		Assert.assertEquals(3, res.size());
		Assert.assertEquals("one", res.get(0));
		Assert.assertEquals("two", res.get(1));
		Assert.assertEquals("three", res.get(2));
	}
	
	@Test
	public void testReduce() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		Integer res = foos.reduce((r,it)->(null==r?it.getNum():r+it.getNum()));
		
		Assert.assertEquals(6, (int)res);
	}
	
	@Test
	public void testReduce2() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		StringBuffer res = foos.reduce((r,it)->r.append(it.getName()).append(","), new StringBuffer());
		
		Assert.assertEquals("one,two,three,", res.toString());
	}
	
	@Test
	public void testFilter() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		List<Foo> res = foos.filter(it->0==it.getNum()%2).toList();
		
		Assert.assertEquals(2, res.size());
		Assert.assertEquals("one", res.get(0).getName());
		Assert.assertEquals("three", res.get(1).getName());
	}

	@Test
	public void testFindFirst() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		Foo res = foos.findFirst(it->2==it.getNum());
		
		Assert.assertEquals(2, (int)res.getNum());
	}
	
		
	@Test
	public void testMap2() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		Map<Integer,String> res = foos.map(it->it.getNum(), it->it.getName());

		Assert.assertEquals("two", res.get(2));
	}
	
	@Test
	public void testPartitioning() {
		DataUtils<Foo> foos = DataUtils.of(Arrays.asList(new Foo("one", 1), new Foo("two", 2), new Foo("three", 3))); 
		Map<Boolean, List<Foo>> res = foos.partitioning(it->0==it.getNum()%2);
		
		List<Foo> odds = res.get(false);
		List<Foo> evens = res.get(true);
		
		Assert.assertEquals(2, odds.size());
		Assert.assertEquals(1, evens.size());
		Assert.assertEquals(1, (int)odds.get(0).getNum());
		Assert.assertEquals(3, (int)odds.get(1).getNum());
		Assert.assertEquals(2, (int)evens.get(0).getNum());
	}
}
