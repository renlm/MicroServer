package cn.renlm.micro.loadbalancer;

import static org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.HintRequestContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 负载均衡策略
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
public class MyHintsLoadBalancerStrategy {

	private final static String DEFAULT_HINT = "DEFAULT";

	@Bean
	public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
			LoadBalancerProperties properties, ConfigurableApplicationContext context) {
		String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
		ServiceInstanceListSupplier supplier = builder().withDiscoveryClient().withCaching().build(context);
		log.info("MyHintsLoadBalancerStrategy loaded.");
		return new HintsLoadBalancer(properties, supplier, name);
	}

	/**
	 * 命中实例
	 */
	public class HintsLoadBalancer implements ReactorServiceInstanceLoadBalancer {

		private final AtomicInteger position;

		private final LoadBalancerProperties properties;

		private final ServiceInstanceListSupplier supplier;

		private final String serviceId;

		public HintsLoadBalancer(LoadBalancerProperties properties,
				ServiceInstanceListSupplier serviceInstanceListSupplier, String serviceId) {
			this.position = new AtomicInteger(new Random().nextInt(1000));
			this.properties = properties;
			this.supplier = serviceInstanceListSupplier;
			this.serviceId = serviceId;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Mono<Response<ServiceInstance>> choose(Request request) {
			String hintFromHeader = this.getHintFromHeader(request);
			String hintDefault = this.getHintDefault(request);
			String hint = StringUtils.hasText(hintFromHeader) ? hintFromHeader : hintDefault;
			log.debug("hint is {}", hint);
			return this.supplier.get().next().map(serviceInstances -> {
				List<ServiceInstance> filtered = this.filteredByHint(serviceInstances, hint, hintDefault);
				return this.getInstanceResponse(filtered);
			});
		}

		private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
			if (instances.isEmpty()) {
				if (log.isWarnEnabled()) {
					log.warn("No servers available for service: " + serviceId);
				}
				return new EmptyResponse();
			}

			// 实例列表
			instances.forEach(instance -> {
				log.debug("serviceId: {}", instance.getServiceId());
				log.debug("instanceId: {}", instance.getInstanceId());
				log.debug("metadata: {}", instance.getMetadata());
			});

			// 一个实例，直接返回
			if (instances.size() == 1) {
				return new DefaultResponse(instances.get(0));
			}

			// 0 ~ Integer.MAX_VALUE 循环取数
			int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
			ServiceInstance instance = instances.get(pos % instances.size());
			return new DefaultResponse(instance);
		}

		private String getHintFromHeader(Object context) {
			String hintHeaderValue = null;
			String hintHeaderName = this.properties.getHintHeaderName();
			if (context instanceof DefaultRequest) {
				Object clientRequest = ((DefaultRequest<?>) context).getContext();
				if (clientRequest instanceof RequestDataContext) {
					RequestDataContext requestContext = (RequestDataContext) clientRequest;
					if (requestContext.getClientRequest() != null) {
						HttpHeaders headers = requestContext.getClientRequest().getHeaders();
						if (headers != null) {
							hintHeaderValue = headers.getFirst(hintHeaderName);
						}
					}
				}
			}
			log.debug("getHintFromHeader - {} : {}", hintHeaderName, hintHeaderValue);
			return hintHeaderValue;
		}

		private String getHintDefault(Object context) {
			String defaultHint = null;
			if (context instanceof DefaultRequest) {
				Object clientRequest = ((DefaultRequest<?>) context).getContext();
				if (clientRequest instanceof RequestDataContext) {
					defaultHint = ((HintRequestContext) clientRequest).getHint();
				}
			}
			defaultHint = StringUtils.hasText(defaultHint) ? defaultHint : DEFAULT_HINT;
			log.debug("getHintDefault is {}", defaultHint);
			return defaultHint;
		}

		private List<ServiceInstance> filteredByHint(List<ServiceInstance> instances, String hint, String hintDefault) {
			if (!StringUtils.hasText(hint)) {
				return instances;
			}

			List<ServiceInstance> filteredInstances = new ArrayList<>();
			for (ServiceInstance serviceInstance : instances) {
				Map<String, String> metadata = serviceInstance.getMetadata();
				String hintFromMetadata = metadata.getOrDefault("hint", hintDefault);
				metadata.put("hint", hintFromMetadata);
				if (hint.equals(hintFromMetadata)) {
					filteredInstances.add(serviceInstance);
				}
			}

			if (filteredInstances.size() > 0) {
				return filteredInstances;
			}

			// 未查到，返回全部
			return instances;
		}

	}

}
