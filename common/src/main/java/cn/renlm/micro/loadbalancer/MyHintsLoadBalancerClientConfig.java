package cn.renlm.micro.loadbalancer;

import static org.springframework.cloud.loadbalancer.support.LoadBalancerEnvironmentPropertyUtils.equalToForClientOrDefault;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 负载均衡
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Configuration(proxyBeanMethods = false)
@Conditional(HintsConfigurationCondition.class)
@LoadBalancerClients(defaultConfiguration = MyHintsLoadBalancerStrategy.class)
public class MyHintsLoadBalancerClientConfig {

}

class HintsConfigurationCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment environment = context.getEnvironment();
		return equalToForClientOrDefault(environment, "configurations", "hints");
	}

}
