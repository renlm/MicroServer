package cn.renlm.micro.eureka;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ Jersey3DiscoveryClientOptionalArgs.class })
public class EurekaServerAuthConfig {

	protected static final String SECRET_KEY_NAME = "eureka.client.secretKey";
	protected static final String SIGN_HEADER_TIMESTAMP = "EUREKA_TIMESTAMP";
	protected static final String SIGN_HEADER_NONCE = "EUREKA_NONCE";
	protected static final String SIGN_HEADER_SIGN = "EUREKA_SIGN";

	@Bean
	@Primary
	public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs(Environment env) {
		String key = env.getProperty(SECRET_KEY_NAME);
		Jersey3DiscoveryClientOptionalArgs discoveryClientOptionalArgs = new Jersey3DiscoveryClientOptionalArgs();
		discoveryClientOptionalArgs.setAdditionalFilters(Collections.singletonList(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				String timestamp = String.valueOf(System.currentTimeMillis());
				String nonce = UUID.randomUUID().toString();
				String secretKey = StringUtils.hasText(key) ? key : UUID.randomUUID().toString();
				String sign = DigestUtils.md5DigestAsHex((timestamp + nonce + secretKey).getBytes());
				requestContext.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
				requestContext.getHeaders().add(SIGN_HEADER_NONCE, nonce);
				requestContext.getHeaders().add(SIGN_HEADER_SIGN, sign);
			}
		}));
		{
			return discoveryClientOptionalArgs;
		}
	}

	@Bean
	EurekaServerAuthFilter eurekaServerAuthFilter(Environment env) {
		String key = env.getProperty(SECRET_KEY_NAME);
		String secretKey = StringUtils.hasText(key) ? key : UUID.randomUUID().toString();
		return new EurekaServerAuthFilter(secretKey);
	}

	@AllArgsConstructor
	public class EurekaServerAuthFilter extends OncePerRequestFilter {

		private String secretKey;

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			String timestamp = request.getHeader(SIGN_HEADER_TIMESTAMP);
			String nonce = request.getHeader(SIGN_HEADER_NONCE);
			String sign = request.getHeader(SIGN_HEADER_SIGN);
			if (StringUtils.hasText(sign)) {
				String md5DigestAsHex = DigestUtils.md5DigestAsHex((timestamp + nonce + secretKey).getBytes());
				if (md5DigestAsHex.equals(sign)) {
					Collection<? extends GrantedAuthority> authorities = Collections.emptySet();
					Authentication token = new UsernamePasswordAuthenticationToken(SIGN_HEADER_SIGN, sign, authorities);
					SecurityContextHolder.getContext().setAuthentication(token);
				}
			}
			{
				filterChain.doFilter(request, response);
			}
		}

	}

}
