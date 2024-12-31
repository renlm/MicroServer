package cn.renlm.micro.loadbalancer;

import java.util.Enumeration;
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
				Enumeration<String> headers = request.getHeaderNames();
				if (Objects.nonNull(headers)) {
					while (headers.hasMoreElements()) {
						String headerName = headers.nextElement();
						String headerValue = request.getHeader(headerName);
						if (StringUtils.hasText(headerValue)) {
							log.debug("openfeign attributes header 透传 - {} : {}", headerName, headerValue);
							template.header(headerName, headerValue);
						}
					}
				}
			} else {
				HttpHeaders httpHeaders = OpenFeignHeadersHolder.get();
				if (Objects.nonNull(httpHeaders)) {
					httpHeaders.forEach((name, values) -> {
						log.debug("openfeign headersHolder header 透传 - {} : {}", name, values);
						template.header(name, values);
					});
				}
			}
		};
	}

}
