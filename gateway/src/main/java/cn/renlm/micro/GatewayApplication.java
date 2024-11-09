package cn.renlm.GrmServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ImportRuntimeHints;

import cn.renlm.GrmServer.aot.hint.MyRuntimeHints;

/**
 * 服务网关
 * 
 * @author RenLiMing(任黎明)
 *
 */
@EnableDiscoveryClient
@SpringBootApplication
@ImportRuntimeHints({ MyRuntimeHints.class })
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
