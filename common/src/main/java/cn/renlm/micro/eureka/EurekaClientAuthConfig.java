package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.security.SecureRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;

import cn.renlm.micro.properties.EurekaClientAuthProperties;
import cn.renlm.micro.util.CsrfUtil;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnClass(RestTemplateDiscoveryClientOptionalArgs.class)
@EnableConfigurationProperties({ EurekaClientAuthProperties.class })
public class EurekaClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	public RestTemplateDiscoveryClientOptionalArgs restTemplateDiscoveryClientOptionalArgs(
			EurekaClientAuthProperties env,
			EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier) {
		RestTemplateDiscoveryClientOptionalArgs discoveryClientOptionalArgs = new RestTemplateDiscoveryClientOptionalArgs(
				eurekaClientHttpRequestFactorySupplier, () -> {
					RestTemplateBuilder builder = new RestTemplateBuilder(restTemplate -> {
						restTemplate.getInterceptors().addFirst(((request, body, execution) -> {
							String serverToken = CsrfUtil.createServerToken();
							String csrfToken = CsrfUtil.createCsrfToken(secureRandom, serverToken);
							String timestamp = String.valueOf(System.currentTimeMillis());
							String secretKey = env.getSecretKey();
							String sign = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
							request.getHeaders().add(X_SERVER_TOKEN, serverToken);
							request.getHeaders().add(X_XSRF_TOKEN, csrfToken);
							request.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
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
