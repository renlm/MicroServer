package cn.renlm.micro.eureka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.netflix.discovery.EurekaClient;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 客户端停机下线
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@ConditionalOnClass({ EurekaClient.class })
public class EurekaClientShutdownHook {

	@Resource
	private EurekaClient eurekaClient;

	@PostConstruct
	public void init() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			eurekaClient.shutdown();
			log.info("EurekaClient 停机下线.");
		}));
	}

}
