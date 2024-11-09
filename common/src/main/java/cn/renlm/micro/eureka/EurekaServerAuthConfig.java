package cn.renlm.micro.eureka;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

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
@ConditionalOnClass(EurekaServerConfigBean.class)
public class EurekaServerAuthConfig {

	@Bean
	EurekaServerAuthFilter eurekaServerAuthFilter() {
		return new EurekaServerAuthFilter();
	}

	public class EurekaServerAuthFilter extends OncePerRequestFilter {

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			log.debug("注册中心认证.");
		}

	}

}
