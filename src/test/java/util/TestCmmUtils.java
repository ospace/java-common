package util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.ospace.common.util.CmmUtils;
import com.tistory.ospace.common.util.FileUtils;


public class TestCmmUtils {
	private static Logger logger = LoggerFactory.getLogger(TestCmmUtils.class);
	
	@Test
	public void testReadFile() throws IOException {
		logger.info("current dir : {}", FileUtils.currentDir());
		String res = FileUtils.readString("src/test/java/resources/test1.txt");
		logger.info("res : " + res);
		
		Assert.assertFalse(null == res || res.isEmpty());
	}
	
	@Test
	public void testCopyObject() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("foo");
		foo.setCreatedDate(LocalDateTime.now());
		
		foo.setData(new ArrayList<Integer>(Arrays.asList(10, 11)));
		
		Bar bar = new Bar();
		
		CmmUtils.copy(foo,  bar);
		
		logger.info("foo[{}]", foo);
		logger.info("bar[{}]", bar);
		
		Assert.assertEquals(foo.getName(), bar.getName());
		Assert.assertNull(bar.getId());
		Assert.assertNull(bar.getCreatedDate());
		Assert.assertEquals(foo.getData().size(), bar.getData().size());
	}
	
	@Test
	public void testEncodeBase32() {
		logger.info("testEncodeBase32 begin");
		
		String data = "ospace";
		String encoded = CmmUtils.encodeBase32(data.getBytes());
		logger.info("testEncodeBase32 encoded[{}]", encoded);
		Assert.assertEquals("N5ZXAYLDMU", encoded);
		
		String decoded = new String(CmmUtils.decodeBase32(encoded));
		logger.info("testEncodeBase32 decoded[{}] size {} vs. {}", decoded, data.length(), decoded.length());
		Assert.assertEquals(data, decoded);
		
		logger.info("testEncodeBase32 end");
	}
	
	@Test
	public void testEncodeBase64() {
		logger.info("testEncodeBase64 begin");
		
		String data = "ospace";
		String encoded = CmmUtils.encodeBase64(data.getBytes());
		logger.info("testEncodeBase64 encoded[{}]", encoded);
		Assert.assertEquals("b3NwYWNl", encoded);
		
		String decoded = new String(CmmUtils.decodeBase64(encoded));
		logger.info("testEncodeBase64 decoded[{}]", decoded);
		Assert.assertEquals(data, decoded);
		
		logger.info("testEncodeBase64 end");		
	}
}
