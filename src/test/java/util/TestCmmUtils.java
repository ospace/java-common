package util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.ospace.common.util.CmmUtils;


public class TestCmmUtils {
	private static Logger logger = LoggerFactory.getLogger(TestCmmUtils.class);
	
	@Test
	public void testReadFile() throws IOException {
		logger.info("current dir : {}", CmmUtils.currentDirectory());
		String res = CmmUtils.readTextFile("src/test/java/resources/test1.txt", 1024);
		logger.info("res : " + res);
		
		Assert.assertFalse(null == res || res.isEmpty());
	}
	
	@Test
	public void testCopyObject() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("foo");
		foo.setCreatedDate(LocalDateTime.now());
		
		Bar bar = new Bar();
		
		CmmUtils.copy(foo,  bar);
		
		logger.info("foo[{}]", foo);
		logger.info("bar[{}]", bar);
		
		Assert.assertEquals(foo.getName(), bar.getName());
		Assert.assertNull(bar.getId());
		Assert.assertNull(bar.getCreatedDate());
	}
}
