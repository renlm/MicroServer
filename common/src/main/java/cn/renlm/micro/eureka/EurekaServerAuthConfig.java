package cn.renlm.micro.eureka;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netflix.appinfo.AbstractEurekaIdentity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ EurekaServerConfigBean.class, SecurityFilterChain.class })
public class EurekaServerAuthConfig {

	protected static final String SIGN_HEADER_TIMESTAMP = "EUREKA_TIMESTAMP";
	protected static final String SIGN_HEADER_NONCE = "EUREKA_NONCE";
	protected static final String SIGN_HEADER_SIGN = "EUREKA_SIGN";

	@Bean
	EurekaServerAuthFilter eurekaServerAuthFilter() {
		return new EurekaServerAuthFilter();
	}

	public class EurekaServerAuthFilter extends OncePerRequestFilter {

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			String timestamp = request.getHeader(SIGN_HEADER_TIMESTAMP);
			String nonce = request.getHeader(SIGN_HEADER_NONCE);
			String sign = request.getHeader(SIGN_HEADER_SIGN);
			String discoveryidentityId = request.getHeader(AbstractEurekaIdentity.AUTH_ID_HEADER_KEY);
			if (StringUtils.hasText(discoveryidentityId)) {
				String md5DigestAsHex = DigestUtils.md5DigestAsHex((timestamp + nonce).getBytes());
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
