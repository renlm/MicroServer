package cn.renlm.micro.filter;

import static cn.renlm.micro.constant.Constants.HINT_HEADER_NAME;

import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import cn.renlm.micro.core.model.rbac.UserClaim;
import cn.renlm.micro.core.sdk.rbac.SessionClient;
import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;

/**
 * 添加负载标记
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class AddHintHeaderGatewayFilter implements GlobalFilter, Ordered {

	@Lazy
	@Resource
	private SessionClient sessionClient;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String hint = request.getHeaders().getFirst(HINT_HEADER_NAME);
		if (!StringUtils.hasText(hint)) {
			UserClaim userClaim = sessionClient.getCurrentUser();
			if (Objects.nonNull(userClaim)) {
				// @formatter:off
				exchange = exchange.mutate().request(req -> req.headers(headers -> headers.set(HINT_HEADER_NAME, userClaim.getHint())).build()).build();
				// @formatter:on
			}
		}
		{
			return chain.filter(exchange);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
