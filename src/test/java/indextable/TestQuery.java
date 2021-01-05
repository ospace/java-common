package indextable;

import static com.tistory.ospace.common.indexedtable.query.QueryOp.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.ospace.common.indexedtable.IndexType;
import com.tistory.ospace.common.indexedtable.IndexedTable;
import com.tistory.ospace.common.indexedtable.ResultSet;
import com.tistory.ospace.common.indexedtable.query.Query;

public class TestQuery {
	@Test
	public void testStartsWidth() {
		IndexedTable<Foo> tbl = new IndexedTable<>();
		tbl.addIndex("name", it->it.name, IndexType.TREE);
		tbl.addIndex("num", it->it.num);
		
		tbl.addAll(Arrays.asList(new Foo("zero", 0), new Foo("zeo", 0), new Foo("one", 1), new Foo("three", 3)));
		
		Query<Foo> query = startsWith("name", "ze");
		
		ResultSet<Foo> res = tbl.query(query);
		
		Assert.assertTrue(2 == res.size());
		
		Iterator<Foo> it  = res.iterator();
		
		Assert.assertTrue("zero".equals(it.next().name));
		Assert.assertTrue("zeo".equals(it.next().name));
	}
}
