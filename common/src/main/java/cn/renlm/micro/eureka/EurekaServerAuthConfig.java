package cn.renlm.micro.eureka;

import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;

import cn.renlm.micro.properties.EurekaClientAuthProperties;
import cn.renlm.micro.util.CsrfTokenUtil;
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

	protected static final String X_SERVER_TOKEN = "X-SERVER-TOKEN";
	protected static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
	protected static final String _CSRF = "_csrf";
	protected static final String SIGN_HEADER_TIMESTAMP = "X-EUREKA-TIMESTAMP";
	protected static final String SIGN_HEADER_SIGN = "X-EUREKA-SIGN";
	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	@Primary
	public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs(EurekaClientAuthProperties env) {
		Jersey3DiscoveryClientOptionalArgs discoveryClientOptionalArgs = new Jersey3DiscoveryClientOptionalArgs();
		discoveryClientOptionalArgs.setAdditionalFilters(Collections.singletonList(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				String timestamp = String.valueOf(System.currentTimeMillis());
				String serverToken = CsrfTokenUtil.createServerToken();
				String csrfToken = CsrfTokenUtil.createCsrfToken(secureRandom, serverToken);
				String secretKey = env.getSecretKey();
				String sign = DigestUtils.md5DigestAsHex((timestamp + csrfToken + secretKey).getBytes());
				requestContext.getHeaders().add(X_SERVER_TOKEN, serverToken);
				requestContext.getHeaders().add(X_XSRF_TOKEN, csrfToken);
				requestContext.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
				requestContext.getHeaders().add(SIGN_HEADER_SIGN, sign);
			}
		}));
		{
			return discoveryClientOptionalArgs;
		}
	}

	@Bean
	EurekaServerAuthFilter eurekaServerAuthFilter(EurekaClientAuthProperties env,
			CsrfTokenRepository csrfTokenRepository) {
		return new EurekaServerAuthFilter(env, csrfTokenRepository);
	}

	public class EurekaServerAuthFilter extends OncePerRequestFilter {

		private EurekaClientAuthProperties env;

		private CsrfTokenRepository csrfTokenRepository;

		public EurekaServerAuthFilter(EurekaClientAuthProperties env, CsrfTokenRepository csrfTokenRepository) {
			this.env = env;
			this.csrfTokenRepository = csrfTokenRepository;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			String timestamp = request.getHeader(SIGN_HEADER_TIMESTAMP);
			String serverToken = request.getHeader(X_SERVER_TOKEN);
			String csrfToken = request.getHeader(X_XSRF_TOKEN);
			String sign = request.getHeader(SIGN_HEADER_SIGN);
			if (hasText(sign)) {
				String secretKey = hasText(env.getSecretKey()) ? env.getSecretKey() : randomUUID().toString();
				String md5DigestAsHex = DigestUtils.md5DigestAsHex((timestamp + csrfToken + secretKey).getBytes());
				if (md5DigestAsHex.equals(sign)) {
					CsrfToken token = new DefaultCsrfToken(X_XSRF_TOKEN, _CSRF, serverToken);
					csrfTokenRepository.saveToken(token, request, response);
					Collection<? extends GrantedAuthority> authorities = Collections.emptySet();
					Authentication auth = new UsernamePasswordAuthenticationToken(SIGN_HEADER_SIGN, sign, authorities);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
			{
				filterChain.doFilter(request, response);
			}
		}

	}

}
