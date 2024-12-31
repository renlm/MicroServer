package cn.renlm.micro.loadbalancer;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.renlm.micro.util.OpenFeignHeadersHolder;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 远程接口调用
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConditionalOnClass(value = { RequestInterceptor.class })
public class MyHintsLoadBalancerFeignConfig {

	private static final Logger log = LoggerFactory.getLogger(MyHintsLoadBalancerStrategy.class);

	@Bean
	RequestInterceptor hintsLoadBalancerHeaderRequestInterceptor(LoadBalancerProperties properties) {
		return template -> {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (Objects.nonNull(attributes)) {
				HttpServletRequest request = attributes.getRequest();
				String hintHeaderName = properties.getHintHeaderName();
				String hintHeaderValue = request.getHeader(hintHeaderName);
				log.debug("openfeign hint header 透传 - {} : {}", hintHeaderName, hintHeaderValue);
				if (StringUtils.hasText(hintHeaderValue)) {
					template.header(hintHeaderName, hintHeaderValue);
				}
			} else {
				HttpHeaders httpHeaders = OpenFeignHeadersHolder.get();
				if (Objects.nonNull(httpHeaders)) {
					httpHeaders.forEach(template::header);
				}
			}
		};
	}

}
