package util;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.tistory.ospace.common.util.CmmUtils;


public class TestCmmUtils {
	private static final Logger logger = Logger.getLogger("TestCmmUtils");
	
	@Test
	public void testReadFile() throws IOException {
		logger.info("current dir : " + CmmUtils.currentDirectory());
		String res = CmmUtils.readTextFile("src/test/java/resources/test1.txt", 1024);
		logger.info("res : " + res);
	}
}
