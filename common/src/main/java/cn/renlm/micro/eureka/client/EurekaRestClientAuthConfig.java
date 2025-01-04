package cn.renlm.micro.eureka.client;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.configuration.SSLContextFactory;
import org.springframework.cloud.configuration.TlsProperties;
import org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestClientDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 客户端认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@ConditionalOnProperty(value = "eureka.client.restclient.enabled", havingValue = "true")
@ConditionalOnClass({ RestClient.class })
@ConditionalOnMissingBean({ EurekaJersey3ClientAuthConfig.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
@AutoConfigureBefore(DiscoveryClientOptionalArgsConfiguration.class)
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
						final String serverToken = CsrfUtil.createServerToken();
						final String csrfToken = CsrfUtil.createCsrfToken(secureRandom, serverToken);
						final String timestamp = String.valueOf(System.currentTimeMillis());
						final String secretKey = env.getSecretKey();
						final String sign = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
						request.getHeaders().add(X_SERVER_TOKEN, serverToken);
						request.getHeaders().add(X_XSRF_TOKEN, csrfToken);
						request.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
						request.getHeaders().add(SIGN_HEADER_SIGN, sign);
						String url = request.getURI().toString();
						if (log.isDebugEnabled()) {
							log.debug("EurekaRestClient request server url: {}, sign: {}", url, sign);
						}
						{
							return execution.execute(request, body);
						}
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
