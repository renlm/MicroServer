package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_NONCE;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.http.WebClientDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WebClientDiscoveryClientOptionalArgs.class)
public class EurekaClientAuthConfig {

	@Bean
	public WebClientDiscoveryClientOptionalArgs webClientDiscoveryClientOptionalArgs() {
		WebClientDiscoveryClientOptionalArgs discoveryClientOptionalArgs = new WebClientDiscoveryClientOptionalArgs(
				() -> {
					String timestamp = String.valueOf(System.currentTimeMillis());
					String nonce = UUID.randomUUID().toString();
					String sign = DigestUtils.md5DigestAsHex((timestamp + nonce).getBytes());
					WebClient.Builder builder = WebClient.builder();
					builder.defaultHeader(SIGN_HEADER_TIMESTAMP, timestamp);
					builder.defaultHeader(SIGN_HEADER_NONCE, nonce);
					builder.defaultHeader(SIGN_HEADER_SIGN, sign);
					return builder;
				});
		{
			return discoveryClientOptionalArgs;
		}
	}

}
