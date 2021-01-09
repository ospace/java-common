package jdk;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDateTime {
	private static Logger logger = LoggerFactory.getLogger(TestDateTime.class);
	
	@Test
	public void testTimeINMilli() {
		long milli1 = new Date().getTime();
		long milli2 = System.currentTimeMillis();
		
		logger.info("{} vs. {}", milli1, milli2);
		
		long diff = milli2 - milli1;
		
		Assert.assertTrue(diff <= 2);
	}
}
