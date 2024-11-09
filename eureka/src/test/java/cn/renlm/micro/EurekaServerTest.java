package cn.renlm.GrmServer;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 测试类
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ActiveProfiles("dev")
@SpringBootTest(classes = EurekaApplication.class)
public class EurekaServerTest {

	private final Logger logger = LoggerFactory.getLogger(EurekaServerTest.class);

	@Test
	public void test() {
		logger.info("test.");
	}

}
