package cn.renlm.micro.eureka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.netflix.discovery.DiscoveryClient;

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
@ConditionalOnClass({ DiscoveryClient.class })
public class EurekaClientShutdownHook {

	@Resource
	private DiscoveryClient discoveryClient;

	@PostConstruct
	public void init() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			discoveryClient.shutdown();
			log.info("停机下线.");
		}));
	}

}
