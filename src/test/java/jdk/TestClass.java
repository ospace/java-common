package jdk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClass {
	private static Logger logger = LoggerFactory.getLogger(TestClass.class);
	
	@Test
	public void testAssignable() {
		Class<?> list1Class = List.class;
		Class<?> list2Class = ArrayList.class;
		
		logger.info("{} vs. {}", list1Class, list2Class);
		
		logger.info("isAssignable: {}", list1Class.isAssignableFrom(list2Class));
		logger.info("isAssignable: {}", list2Class.isAssignableFrom(list1Class));
		logger.info("Collection isAssignable: {}", Collection.class.isAssignableFrom(list1Class));
		logger.info("Collection isAssignable: {}", Collection.class.isAssignableFrom(list2Class));
		
	}
}
