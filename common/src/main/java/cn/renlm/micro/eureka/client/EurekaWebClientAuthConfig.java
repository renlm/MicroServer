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
import org.springframework.cloud.netflix.eureka.http.WebClientTransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka客户端认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@ConditionalOnProperty(value = "eureka.client.webclient.enabled", havingValue = "true")
@ConditionalOnClass({ WebClient.class })
@ConditionalOnMissingBean({ EurekaJersey3ClientAuthConfig.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaWebClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	private WebClient.Builder eurekaWebClientBuilder(EurekaAuthProperties env) {
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
			String url = req.url().toString();
			log.debug("EurekaWebClient request server url: {}", url);
			return next.exchange(request.build());
		});
	}

	@Bean
	@Primary
	public WebClientDiscoveryClientOptionalArgs webClientDiscoveryClientOptionalArgs(EurekaAuthProperties env,
			TlsProperties tlsProperties) throws GeneralSecurityException, IOException {
		WebClientDiscoveryClientOptionalArgs args = new WebClientDiscoveryClientOptionalArgs(() -> {
			return this.eurekaWebClientBuilder(env);
		});
		if (tlsProperties.isEnabled()) {
			SSLContextFactory factory = new SSLContextFactory(tlsProperties);
			args.setSSLContext(factory.createSSLContext());
		}
		{
			return args;
		}
	}

	@Bean
	@Primary
	public WebClientTransportClientFactories webClientTransportClientFactories(EurekaAuthProperties env) {
		return new WebClientTransportClientFactories(() -> {
			return this.eurekaWebClientBuilder(env);
		});
	}

}
