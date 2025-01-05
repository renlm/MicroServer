package cn.renlm.micro.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Lazy;

import com.netflix.discovery.DiscoveryClient;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * CoreWeb
 * 
 * @author RenLiMing(任黎明)
 *
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class CoreWebApplication {

	@Lazy
	@Resource
	private DiscoveryClient discoveryClient;

	public static void main(String[] args) {
		SpringApplication.run(CoreWebApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			discoveryClient.shutdown();
		}));
	}

}
