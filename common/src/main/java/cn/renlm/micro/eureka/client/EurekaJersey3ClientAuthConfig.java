package cn.renlm.micro.eureka.client;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_SIGN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.SIGN_HEADER_TIMESTAMP;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_SERVER_TOKEN;
import static cn.renlm.micro.eureka.EurekaServerAuthConfig.X_XSRF_TOKEN;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.DigestUtils;

import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 客户端认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@ConditionalOnProperty(value = "eureka.client.jersey.enabled", havingValue = "true")
@ConditionalOnClass({ Jersey3DiscoveryClientOptionalArgs.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaJersey3ClientAuthConfig {

	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	@Primary
	public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs(EurekaAuthProperties env) {
		Jersey3DiscoveryClientOptionalArgs args = new Jersey3DiscoveryClientOptionalArgs();
		args.setAdditionalFilters(Collections.singletonList(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				final String serverToken = CsrfUtil.createServerToken();
				final String csrfToken = CsrfUtil.createCsrfToken(secureRandom, serverToken);
				final String timestamp = String.valueOf(System.currentTimeMillis());
				final String secretKey = env.getSecretKey();
				final String sign = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
				requestContext.getHeaders().add(X_SERVER_TOKEN, serverToken);
				requestContext.getHeaders().add(X_XSRF_TOKEN, csrfToken);
				requestContext.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
				requestContext.getHeaders().add(SIGN_HEADER_SIGN, sign);
				String url = requestContext.getUri().toString();
				log.debug("EurekaJersey3Client request server url: {}", url);
			}
		}));
		{
			return args;
		}
	}

}
