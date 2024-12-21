package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.security.SecureRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.WebClientDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;

/**
 * WebClient（Eureka客户端认证）
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnClass(WebClient.class)
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaWebClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	public WebClientDiscoveryClientOptionalArgs webClientDiscoveryClientOptionalArgs(EurekaAuthProperties env,
			EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier) {
		WebClientDiscoveryClientOptionalArgs discoveryClientOptionalArgs = new WebClientDiscoveryClientOptionalArgs(
				() -> {
					return WebClient.builder().filter((request, next) -> {
						String serverToken = CsrfUtil.createServerToken();
						String csrfToken = CsrfUtil.createCsrfToken(secureRandom, serverToken);
						String timestamp = String.valueOf(System.currentTimeMillis());
						String secretKey = env.getSecretKey();
						String sign = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
						request.headers().add(X_SERVER_TOKEN, serverToken);
						request.headers().add(X_XSRF_TOKEN, csrfToken);
						request.headers().add(SIGN_HEADER_TIMESTAMP, timestamp);
						request.headers().add(SIGN_HEADER_SIGN, sign);
						return next.exchange(request);
					});
				});
		{
			return discoveryClientOptionalArgs;
		}
	}

}
