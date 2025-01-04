package cn.renlm.micro.eureka;

import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.server.ReplicationClientAdditionalFilters;
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

import cn.renlm.micro.properties.EurekaAuthProperties;
import cn.renlm.micro.util.CsrfUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@ConditionalOnClass({ ReplicationClientAdditionalFilters.class })
@EnableConfigurationProperties({ EurekaAuthProperties.class })
public class EurekaServerAuthConfig {

	public static final String X_SERVER_TOKEN = "x-server-token";
	public static final String X_XSRF_TOKEN = "x-xsrf-token";
	public static final String _CSRF = "_csrf";
	public static final String SIGN_HEADER_TIMESTAMP = "x-eureka-timestamp";
	public static final String SIGN_HEADER_SIGN = "x-eureka-sign";
	private SecureRandom secureRandom = new SecureRandom();

	@Bean
	@Primary
	public ReplicationClientAdditionalFilters replicationClientAdditionalFilters(EurekaAuthProperties env) {
		return new ReplicationClientAdditionalFilters(Collections.singletonList(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				String serverToken = CsrfUtil.createServerToken();
				String csrfToken = CsrfUtil.createCsrfToken(secureRandom, serverToken);
				String timestamp = String.valueOf(System.currentTimeMillis());
				String secretKey = env.getSecretKey();
				String sign = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
				requestContext.getHeaders().add(X_SERVER_TOKEN, serverToken);
				requestContext.getHeaders().add(X_XSRF_TOKEN, csrfToken);
				requestContext.getHeaders().add(SIGN_HEADER_TIMESTAMP, timestamp);
				requestContext.getHeaders().add(SIGN_HEADER_SIGN, sign);
			}
		}));
	}

	@Bean
	EurekaServerAuthFilter eurekaServerAuthFilter(EurekaAuthProperties env, CsrfTokenRepository csrfTokenRepository) {
		return new EurekaServerAuthFilter(env, csrfTokenRepository);
	}

	public class EurekaServerAuthFilter extends OncePerRequestFilter {

		private EurekaAuthProperties env;

		private CsrfTokenRepository csrfTokenRepository;

		public EurekaServerAuthFilter(EurekaAuthProperties env, CsrfTokenRepository csrfTokenRepository) {
			this.env = env;
			this.csrfTokenRepository = csrfTokenRepository;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			String serverToken = request.getHeader(X_SERVER_TOKEN);
			String csrfToken = request.getHeader(X_XSRF_TOKEN);
			String timestamp = request.getHeader(SIGN_HEADER_TIMESTAMP);
			String sign = request.getHeader(SIGN_HEADER_SIGN);
			String requestURI = request.getRequestURI();
			if (hasText(sign)) {
				String secretKey = hasText(env.getSecretKey()) ? env.getSecretKey() : randomUUID().toString();
				String md5DigestAsHex = DigestUtils.md5DigestAsHex((csrfToken + timestamp + secretKey).getBytes());
				if (md5DigestAsHex.equals(sign)) {
					CsrfToken token = new DefaultCsrfToken(X_XSRF_TOKEN, _CSRF, serverToken);
					csrfTokenRepository.saveToken(token, request, response);
					Collection<? extends GrantedAuthority> authorities = Collections.emptySet();
					Authentication auth = new UsernamePasswordAuthenticationToken(SIGN_HEADER_SIGN, sign, authorities);
					SecurityContextHolder.getContext().setAuthentication(auth);
				} else {
					log.debug("err sign: {}", requestURI);
				}
			} else {
				log.debug("no sign: {}", requestURI);
			}
			{
				filterChain.doFilter(request, response);
			}
		}

	}

}
