package cn.renlm.micro.filter;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.expand;
import static org.springframework.util.StringUtils.hasText;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.constant.Constants;
import cn.renlm.micro.filter.AddHintHeaderGatewayFilterFactory.Config;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

/**
 * 添加负载标记 
 * <p>
 * filters: 
 * - MapRequestHeader
 * - MapRequestHeader={hintHeaderName}, {hintHeaderValue}
 * </p>
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class AddHintHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

	private static final String HINT_NAME_KEY = "HEADER_NAME";

	private static final String HINT_VALUE_KEY = "HINT_VALUE";

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	public AddHintHeaderGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(HINT_NAME_KEY, HINT_VALUE_KEY);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new GatewayFilter() {
			@Override
			@SneakyThrows
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				// 优先取配置指定[路由标记]
				String name = hasText(config.headerName) ? config.headerName : Constants.X_LB_HINT;
				String hint = hasText(config.hintValue) ? expand(exchange, config.hintValue) : config.hintValue;
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				}
				// 其次从请求头获取[路由标记]
				ServerHttpRequest request = exchange.getRequest();
				HttpHeaders httpHeaders = request.getHeaders();
				hint = httpHeaders.getFirst(Constants.X_LB_HINT);
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				}
				// 最后匹配当前节点的[路由标记]
				Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
				hint = metadataMap.get(Constants.METADATA_HINT);
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				} else {
					return chain.filter(exchange);
				}
			}

			@Override
			public String toString() {
				// @formatter:off
				return filterToStringCreator(AddHintHeaderGatewayFilterFactory.this)
						.append(HINT_NAME_KEY, config.headerName)
						.append(HINT_VALUE_KEY, config.hintValue)
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

		private String headerName;

		private String hintValue;

	}

}
