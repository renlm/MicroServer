package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_NONCE;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static java.util.UUID.randomUUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;

import cn.renlm.micro.properties.EurekaClientAuthProperties;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnClass(RestTemplateDiscoveryClientOptionalArgs.class)
@EnableConfigurationProperties({ EurekaClientAuthProperties.class })
public class EurekaClientAuthConfig {

	@Bean
	public RestTemplateDiscoveryClientOptionalArgs restTemplateDiscoveryClientOptionalArgs(
			EurekaClientAuthProperties env,
			EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier) {
		RestTemplateDiscoveryClientOptionalArgs discoveryClientOptionalArgs = new RestTemplateDiscoveryClientOptionalArgs(
				eurekaClientHttpRequestFactorySupplier, () -> {
					RestTemplateBuilder builder = new RestTemplateBuilder(restTemplate -> {
						restTemplate.getInterceptors().addFirst(((request, body, execution) -> {
							String timestamp = String.valueOf(System.currentTimeMillis());
							String nonce = randomUUID().toString();
							String secretKey = env.getSecretKey();
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
