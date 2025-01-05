package cn.renlm.micro.filter;

import static cn.renlm.micro.constant.Constants.HINT_DEFAULT_CONFIG;
import static cn.renlm.micro.constant.Constants.X_LB_HINT;
import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.expand;
import static org.springframework.util.StringUtils.hasText;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import cn.renlm.micro.filter.AddHintHeaderGatewayFilterFactory.Config;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

/**
 * 添加负载标记
 * <p>
 * AddHintHeader={hint}
 * </p>
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class AddHintHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

	@Resource
	private LoadBalancerProperties properties;

	public AddHintHeaderGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(VALUE_KEY);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new GatewayFilter() {
			@Override
			@SneakyThrows
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				String defaultName = properties.getHintHeaderName();
				String defaultHint = properties.getHint().get(HINT_DEFAULT_CONFIG);
				// 优先取配置指定[负载标记]
				String name = hasText(defaultName) ? defaultName : X_LB_HINT;
				String hint = hasText(config.hint) ? expand(exchange, config.hint) : defaultHint;
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				}
				// 其次从请求头获取[负载标记]
				ServerHttpRequest request = exchange.getRequest();
				HttpHeaders httpHeaders = request.getHeaders();
				hint = httpHeaders.getFirst(name);
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				}
				// 最后从Cookie获取[负载标记]
				MultiValueMap<String, HttpCookie> cookies = request.getCookies();
				HttpCookie cookie = cookies.getFirst(name);
				if (Objects.nonNull(cookie)) {
					hint = cookie.getValue();
					if (hasText(hint)) {
						return addHeader(exchange, chain, name, hint);
					}
				}
				{
					return chain.filter(exchange);
				}
			}

			@Override
			public String toString() {
				// @formatter:off
				return filterToStringCreator(AddHintHeaderGatewayFilterFactory.this)
						.append(VALUE_KEY, config.hint)
						.toString();
				// @formatter:on
			}
		};
	}

	private Mono<Void> addHeader(ServerWebExchange exchange, GatewayFilterChain chain, String name, String value) {
		// @formatter:off
		ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.set(name, value)).build();
		return chain.filter(exchange.mutate().request(request).build());
		// @formatter:on
	}

	@Data
	public static class Config {

		private String hint;

	}

}
