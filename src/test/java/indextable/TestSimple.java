package indextable;

import static com.tistory.ospace.common.indextable.query.QueryOp.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.tistory.ospace.common.indextable.IndexType;
import com.tistory.ospace.common.indextable.ResultSet;
import com.tistory.ospace.common.indextable.SimpleIndexTable;
import com.tistory.ospace.common.indextable.query.Query;

public class TestSimple {
	public static class Item {
		private String name;
		private Integer num;
		private String[] names;
		private List<Integer> nums;
		
		
		public static Item of(String name, Integer num) {
			Item ret = new Item();
			ret.setName(name);
			ret.setNum(num);
			String[] names = {"0:"+name, "1:"+name};
			ret.setNames(names);
			ret.setNums(Arrays.asList(1*num,2*num));
			
			return ret;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getNum() {
			return num;
		}
		public void setNum(Integer num) {
			this.num = num;
		}
		
		
		public String[] getNames() {
			return names;
		}

		public void setNames(String[] names) {
			this.names = names;
		}

		public List<Integer> getNums() {
			return nums;
		}

		public void setNums(List<Integer> nums) {
			this.nums = nums;
		}

		@Override
		public String toString() {
			return String.format("{name:%s, num:%s, nums:%s, names:%s}", getName(), getNum(), getNums().toString(), getNames());
		}
	}
	
	static SimpleIndexTable<Item> generateIndexTable() {
		SimpleIndexTable<Item> index = SimpleIndexTable.of(Item.class);//, "name", "num");
		index.addIndex("name", it->it.name);
		index.addIndex("num", it->it.num, IndexType.TREE);
		index.addIndex("nums", it->it.nums, IndexType.TREE);
		index.addIndex("names", it->Arrays.asList(it.names));

		List<Item> items = new ArrayList<>();
		for(int i=0; i<10; ++i) {
			items.add(Item.of(weekStr(), i%10));
		}
		
		//System.out.println("data : " + items);

		index.addAll(items);
		
		return index;
	}
	
	static String[] week= {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	
	static String weekStr() {
		return week[ThreadLocalRandom.current().nextInt(0, week.length)];
	}
	
	static void runtime(Runnable runnable) {
		List<Long> runtimes = new ArrayList<>();
		long begin_time = 0, end_time = 0;
		for(int i=0; i<10; ++i) {
			begin_time = System.currentTimeMillis();
			runnable.run();
			end_time = System.currentTimeMillis();
			runtimes.add(end_time-begin_time);
		}
		
		runtimes.sort((l,r)->l.compareTo(r));
		
		runtimes.remove(runtimes.size()-1);
		runtimes.remove(0);
		
		System.out.println("runtimes : " + runtimes);
		System.out.println("runtime(msec) : " + runtimes.stream().mapToLong(it->it).average().getAsDouble());
	}
	
	@Test
	public void testSimple() {
		TreeMap<String, Integer> data = new TreeMap<>();
		for(int i=0;  i<15; ++i) {
			data.put(""+i, i);
		}
		//System.out.println("subMap : " + data.subMap("3", "10"));
		System.out.println("subMap : " + data.subMap("10", true, "3", true));

		long begin_time = System.currentTimeMillis();
		SimpleIndexTable<Item> index2 = generateIndexTable();
		long end_time = System.currentTimeMillis();
		
		System.out.println("init  time  " + (end_time-begin_time) + " msec");
		//System.out.println("index : " + index2.getIndex("name"));
		System.out.println("index : " + index2.getIndex("name").getClass().getTypeName());
		
		Query<Item> query = and(eq("name", "Monday"), between("num", 2, 8), between("nums", 1, 10), eq("names", "1:Wednesday"));
		
		runtime(()->{
			ResultSet<Item> res2 = index2.query(query);
			System.out.println("res : "+(res2==null ? 0 : res2.size())+ " ea, data : " + res2);
		});
		
		ResultSet<Item> res = null;
		res = index2.query(lt("nums", 8));
		System.out.println(">>> "+res);
		//print(res);
		
		res = index2.query(lte("nums", 8));
		System.out.println(">>> "+res);
	}
	
	@Test
	public void testListQuery() {
		
		SimpleIndexTable<Item> index = generateIndexTable();
		Query<Item> query = in("name", Arrays.asList("Monday", "Wednesday"));
		ResultSet<Item> res = null;
		res = index.query(query);
		System.out.println(">>> "+res);
	}
}
