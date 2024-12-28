package cn.renlm.micro.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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

	public static void main(String[] args) {
		SpringApplication.run(CoreWebApplication.class, args);
	}

}
