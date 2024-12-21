package cn.renlm.micro.eureka.client;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.configuration.SSLContextFactory;
import org.springframework.cloud.configuration.TlsProperties;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestClientDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;

/**
 * Eureka客户端认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnProperty(value = "eureka.client.restclient.enabled", havingValue = "true")
@ConditionalOnClass({ RestClient.class })
@ConditionalOnMissingBean({ EurekaJersey3ClientAuthConfig.class })
@ConditionalOnBean({ EurekaClientHttpRequestFactorySupplier.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaRestClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	@Primary
	public RestClientDiscoveryClientOptionalArgs restClientDiscoveryClientOptionalArgs(EurekaAuthProperties env,
			TlsProperties tlsProperties, EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier,
			ObjectProvider<RestClient.Builder> restClientBuilderProvider) throws GeneralSecurityException, IOException {
		RestClientDiscoveryClientOptionalArgs args = new RestClientDiscoveryClientOptionalArgs(
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
		if (tlsProperties.isEnabled()) {
			SSLContextFactory factory = new SSLContextFactory(tlsProperties);
			args.setSSLContext(factory.createSSLContext());
		}
		{
			return args;
		}
	}

}
