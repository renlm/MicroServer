package cn.renlm.micro.loadbalancer;

import static cn.renlm.micro.constant.Constants.COOKIE_HEADER_NAME;
import static cn.renlm.micro.constant.Constants.X_XSRF_TOKEN_HEADER_NAME;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
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
		String hintHeaderName = properties.getHintHeaderName();
		return template -> {
			ServletRequestAttributes attributes = (ServletRequestAttributes) getRequestAttributes();
			if (Objects.nonNull(attributes)) {
				HttpServletRequest request = attributes.getRequest();
				String hintHeaderValue = request.getHeader(hintHeaderName);
				if (StringUtils.hasText(hintHeaderValue)) {
					log.debug("openfeign attributes header 透传 - {} : {}", hintHeaderName, hintHeaderValue);
					template.header(hintHeaderName, hintHeaderValue);
				}
			} else {
				HttpHeaders httpHeaders = OpenFeignHeadersHolder.get();
				if (Objects.nonNull(httpHeaders)) {
					String[] headerNames = { hintHeaderName, X_XSRF_TOKEN_HEADER_NAME, COOKIE_HEADER_NAME };
					for (String headerName : headerNames) {
						List<String> headerValues = httpHeaders.getValuesAsList(headerName);
						if (Objects.nonNull(headerValues) && headerValues.size() > 0) {
							log.debug("openfeign headersHolder header 透传 - {} : {}", headerName, headerValues);
							template.header(headerName, headerValues);
						}
					}
				}
			}
		};
	}

}
