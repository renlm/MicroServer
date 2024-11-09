package cn.renlm.micro;

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
@SpringBootTest(classes = GatewayApplication.class)
public class GatewayServerTest {

	private final Logger logger = LoggerFactory.getLogger(GatewayServerTest.class);

	@Test
	public void test() {
		logger.info("test.");
	}

}
