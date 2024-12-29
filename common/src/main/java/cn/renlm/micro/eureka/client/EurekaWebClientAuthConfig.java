package cn.renlm.micro.eureka.client;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.security.SecureRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;

/**
 * Eureka客户端认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnProperty(value = "eureka.client.webclient.enabled", havingValue = "true")
@ConditionalOnClass({ WebClient.class })
@ConditionalOnMissingBean({ EurekaJersey3ClientAuthConfig.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaWebClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	public WebClient.Builder eurekaWebClientBuilder(EurekaAuthProperties env) {
		return WebClient.builder().filter((req, next) -> {
			final String serverToken = CsrfUtil.createServerToken();
			final String csrfToken = CsrfUtil.createCsrfToken(secureRandom, serverToken);
			final String timestamp = String.valueOf(System.currentTimeMillis());
			final String secretKey = env.getSecretKey();
			final String sign = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
			final ClientRequest.Builder request = ClientRequest.from(req);
			request.header(X_SERVER_TOKEN, serverToken);
			request.header(X_XSRF_TOKEN, csrfToken);
			request.header(SIGN_HEADER_TIMESTAMP, timestamp);
			request.header(SIGN_HEADER_SIGN, sign);
			return next.exchange(request.build());
		});
	}

}
