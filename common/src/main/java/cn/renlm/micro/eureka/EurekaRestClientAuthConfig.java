package cn.renlm.micro.eureka;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.security.SecureRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestClientDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;

/**
 * RestClient（Eureka客户端认证）
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnClass(RestClient.class)
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaRestClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	public RestClientDiscoveryClientOptionalArgs restClientDiscoveryClientOptionalArgs(EurekaAuthProperties env,
			EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier) {
		RestClientDiscoveryClientOptionalArgs discoveryClientOptionalArgs = new RestClientDiscoveryClientOptionalArgs(
				eurekaClientHttpRequestFactorySupplier, () -> {
					return RestClient.builder().requestInterceptor(((request, body, execution) -> {
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
		{
			return discoveryClientOptionalArgs;
		}
	}

}
