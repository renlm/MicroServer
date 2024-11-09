package cn.renlm.micro.eureka;

import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;

import cn.renlm.micro.properties.EurekaClientAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnClass({ Jersey3DiscoveryClientOptionalArgs.class })
@EnableConfigurationProperties({ EurekaClientAuthProperties.class })
public class EurekaServerAuthConfig {

	protected static final String SIGN_HEADER_TIMESTAMP = "EUREKA_TIMESTAMP";
	protected static final String SIGN_HEADER_NONCE = "EUREKA_NONCE";
	protected static final String SIGN_HEADER_SIGN = "EUREKA_SIGN";

	@Bean
	@Primary
	public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs(EurekaClientAuthProperties env) {
		Jersey3DiscoveryClientOptionalArgs discoveryClientOptionalArgs = new Jersey3DiscoveryClientOptionalArgs();
		discoveryClientOptionalArgs.setAdditionalFilters(Collections.singletonList(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				String timestamp = String.valueOf(System.currentTimeMillis());
				String nonce = UUID.randomUUID().toString();
				String secretKey = env.getSecretKey();
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
	EurekaServerAuthFilter eurekaServerAuthFilter(EurekaClientAuthProperties env) {
		return new EurekaServerAuthFilter(env);
	}

	public class EurekaServerAuthFilter extends OncePerRequestFilter {

		private EurekaClientAuthProperties env;

		public EurekaServerAuthFilter(EurekaClientAuthProperties env) {
			this.env = env;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			String timestamp = request.getHeader(SIGN_HEADER_TIMESTAMP);
			String nonce = request.getHeader(SIGN_HEADER_NONCE);
			String sign = request.getHeader(SIGN_HEADER_SIGN);
			if (hasText(sign)) {
				String secretKey = hasText(env.getSecretKey()) ? env.getSecretKey() : randomUUID().toString();
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
