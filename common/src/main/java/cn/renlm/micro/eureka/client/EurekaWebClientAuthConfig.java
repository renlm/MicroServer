package cn.renlm.micro.eureka.client;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.configuration.SSLContextFactory;
import org.springframework.cloud.configuration.TlsProperties;
import org.springframework.cloud.netflix.eureka.http.WebClientDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.DigestUtils;
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
@ConditionalOnMissingBean({ EurekaJersey3ClientAuthConfig.class })
@ConditionalOnClass({ WebClient.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaWebClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	@Primary
	public WebClientDiscoveryClientOptionalArgs webClientDiscoveryClientOptionalArgs(EurekaAuthProperties env,
			TlsProperties tlsProperties) throws GeneralSecurityException, IOException {
		WebClientDiscoveryClientOptionalArgs args = new WebClientDiscoveryClientOptionalArgs(() -> {
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
		if (tlsProperties.isEnabled()) {
			SSLContextFactory factory = new SSLContextFactory(tlsProperties);
			args.setSSLContext(factory.createSSLContext());
		}
		{
			return args;
		}
	}

}
