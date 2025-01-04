package cn.renlm.micro.filter;

import static cn.renlm.micro.constant.Constants.HINT_HEADER_NAME;
import static cn.renlm.micro.constant.Constants.HINT_KEY;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;
import static org.springframework.util.StringUtils.hasText;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.common.Resp;
import cn.renlm.micro.core.model.rbac.UserClaim;
import cn.renlm.micro.core.sdk.rbac.SessionClient;
import cn.renlm.micro.filter.AddHintHeaderGatewayFilterFactory.Config;
import cn.renlm.micro.util.OpenFeignHeadersHolder;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

/**
 * 添加负载标记
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class AddHintHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

	private static final String NAME_KEY = "name";

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	@Lazy
	@Resource
	private SessionClient sessionClient;

	public AddHintHeaderGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(NAME_KEY, HINT_KEY);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new GatewayFilter() {
			@Override
			@SneakyThrows
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				// 优先取配置指定[路由标记]
				String name = hasText(config.name) ? config.name : HINT_HEADER_NAME;
				String hint = hasText(config.hint) ? ServerWebExchangeUtils.expand(exchange, config.hint) : config.hint;
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				}
				// 其次从请求头获取[路由标记]
				ServerHttpRequest request = exchange.getRequest();
				HttpHeaders httpHeaders = request.getHeaders();
				hint = httpHeaders.getFirst(HINT_HEADER_NAME);
				if (hasText(hint)) {
					return addHeader(exchange, chain, name, hint);
				}
				// 然后根据当前登录用户查询[路由标记]
				Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
				String defaults = metadataMap.get(HINT_KEY);
				if (Objects.nonNull(httpHeaders)) {
					HttpHeaders mutateHttpHeaders = new HttpHeaders(httpHeaders);
					Resp<UserClaim> resp = supplyAsync(() -> {
						mutateHttpHeaders.add(name, defaults);
						OpenFeignHeadersHolder.set(mutateHttpHeaders);
						return sessionClient.getCurrentUser();
					}).get();
					UserClaim userClaim = resp.getData();
					if (resp.isOk() && Objects.nonNull(userClaim)) {
						hint = userClaim.getHint();
						if (hasText(hint)) {
							return addHeader(exchange, chain, name, hint);
						}
					}
				}
				// 最后匹配当前节点的[路由标记]
				if (hasText(defaults)) {
					return addHeader(exchange, chain, name, defaults);
				} else {
					return chain.filter(exchange);
				}
			}

			@Override
			public String toString() {
				// @formatter:off
				return filterToStringCreator(AddHintHeaderGatewayFilterFactory.this)
						.append(NAME_KEY, config.name)
						.append(HINT_KEY, config.hint)
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

		private String name = HINT_HEADER_NAME;

		private String hint;

	}

}
