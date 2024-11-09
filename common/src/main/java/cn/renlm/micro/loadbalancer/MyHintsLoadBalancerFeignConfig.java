package cn.renlm.micro.loadbalancer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 远程接口调用
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = { RequestInterceptor.class })
public class MyHintsLoadBalancerFeignConfig {

	@Bean
	RequestInterceptor hintsLoadBalancerHeaderRequestInterceptor(LoadBalancerProperties properties) {
		return template -> {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (null != attributes) {
				HttpServletRequest request = attributes.getRequest();
				String hintHeaderName = properties.getHintHeaderName();
				String hintHeaderValue = request.getHeader(hintHeaderName);
				log.debug("openfeign hint header 透传 - {} : {}", hintHeaderName, hintHeaderValue);
				if (StringUtils.hasText(hintHeaderValue)) {
					template.header(hintHeaderName, hintHeaderValue);
				}
			}
		};
	}

}
