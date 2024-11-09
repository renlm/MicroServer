package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SECRET_KEY_NAME;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_NONCE;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RestTemplateDiscoveryClientOptionalArgs.class)
public class EurekaClientAuthConfig {

	@Bean
	public RestTemplateDiscoveryClientOptionalArgs restTemplateDiscoveryClientOptionalArgs(Environment env,
			EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier) {
		String key = env.getProperty(SECRET_KEY_NAME);
		RestTemplateDiscoveryClientOptionalArgs discoveryClientOptionalArgs = new RestTemplateDiscoveryClientOptionalArgs(
				eurekaClientHttpRequestFactorySupplier, () -> {
					RestTemplateBuilder builder = new RestTemplateBuilder(restTemplate -> {
						restTemplate.getInterceptors().addFirst(((request, body, execution) -> {
							String timestamp = String.valueOf(System.currentTimeMillis());
							String nonce = UUID.randomUUID().toString();
							String secretKey = StringUtils.hasText(key) ? key : UUID.randomUUID().toString();
							String sign = DigestUtils.md5DigestAsHex((timestamp + nonce + secretKey).getBytes());
							request.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
							request.getHeaders().add(SIGN_HEADER_NONCE, nonce);
							request.getHeaders().add(SIGN_HEADER_SIGN, sign);
							return execution.execute(request, body);
						}));
					});
					return builder;
				});
		{
			return discoveryClientOptionalArgs;
		}
	}

}
