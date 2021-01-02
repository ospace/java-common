package indextable;

import static com.tistory.ospace.common.indextable.query.QueryOp.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.ospace.common.indextable.SimpleIndexTable;
import com.tistory.ospace.common.indextable.core.ResultSet;
import com.tistory.ospace.common.indextable.query.Query;


public class TestHolder {
	@Test
	public void testSimpleHolder() {
		SimpleIndexTable<Foo> index2 = new SimpleIndexTable<>();
		
		index2.addIndex("name", it->it.name);
		index2.addIndex("num", it->it.num);
		
		index2.addAll(Arrays.asList(new Foo("zero", 0), new Foo("one", 1), new Foo("two", 2), new Foo("three", 3)));
		
		Query<Foo> query1 = eq("name", "{0}");
		ResultSet<Foo> res = null;
		
		res = index2.query(query1, "one");
		res.forEach(it->System.out.println("1>> " + it));
		
		Assert.assertTrue(1 == res.size());
		Assert.assertTrue(1 == res.iterator().next().num);
		
		Query<Foo> query2 = in("num", "{0}", "{1}");
		res = index2.query(query2, 1, 3);
		res.forEach(it->System.out.println("2>> " + it));
		Assert.assertTrue(2 == res.size());
		Iterator<Foo> it  = res.iterator();
		
		Assert.assertTrue("one".equals(it.next().name));
		Assert.assertTrue("three".equals(it.next().name));
	}
}
