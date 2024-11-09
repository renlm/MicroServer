package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_NONCE;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jersey3DiscoveryClientOptionalArgs.class)
public class EurekaClientAuthConfig {

	@Bean
	public Jersey3DiscoveryClientOptionalArgs discoveryClientOptionalArgs() {
		Jersey3DiscoveryClientOptionalArgs discoveryClientOptionalArgs = new Jersey3DiscoveryClientOptionalArgs();
		discoveryClientOptionalArgs.setAdditionalFilters(Collections.singletonList(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				String timestamp = String.valueOf(System.currentTimeMillis());
				String nonce = UUID.randomUUID().toString();
				String sign = DigestUtils.md5DigestAsHex((timestamp + nonce).getBytes());
				requestContext.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
				requestContext.getHeaders().add(SIGN_HEADER_NONCE, nonce);
				requestContext.getHeaders().add(SIGN_HEADER_SIGN, sign);
			}
		}));
		{
			return discoveryClientOptionalArgs;
		}
	}

}
