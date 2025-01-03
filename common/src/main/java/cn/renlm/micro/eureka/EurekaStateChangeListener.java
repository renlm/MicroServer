package cn.renlm.micro.eureka;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.netflix.appinfo.InstanceInfo;

import cn.renlm.micro.constant.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 事件
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Component
@ConditionalOnClass( name = "org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent")
public class EurekaStateChangeListener {

	@EventListener
	public void listen(EurekaInstanceRegisteredEvent event) {
		InstanceInfo instanceInfo = event.getInstanceInfo();
		instanceInfo.getMetadata();
		this.update("服务注册事件", instanceInfo);
	}

	@EventListener
	public void listen(EurekaInstanceRenewedEvent event) {
		InstanceInfo instanceInfo = event.getInstanceInfo();
		this.update("服务续约事件", instanceInfo);
	}

	/**
	 * 约定客户端优先采用 Deployment Headless
	 * 
	 * @param event
	 * @param instanceInfo
	 */
	private void update(String event, InstanceInfo instanceInfo) {
		String appName = instanceInfo.getAppName();
		Map<String, String> metadata = instanceInfo.getMetadata();
		String podIp = metadata.get(Constants.POD_IP_KEY);
		String podServiceName = metadata.get(Constants.POD_SERVICE_NAME_KEY);
		String podNamespace = metadata.get(Constants.POD_NAMESPACE_KEY);
		String securePort = String.valueOf(instanceInfo.getPort());
		if (StringUtils.hasText(podIp) && StringUtils.hasText(podServiceName) && StringUtils.hasText(podNamespace)) {
			String podName = StringUtils.replace(podIp, ".", "-");
			String hostName = podName + "." + podServiceName + "." + podNamespace + ".svc.cluster.local";
			String instanceId = hostName + ":" + securePort;
			log.debug("{} - appName: {}, hostName: {}, instanceId: {}", event, appName, hostName, instanceId);
			{
				Field field = ReflectionUtils.findField(InstanceInfo.class, "instanceId");
				field.setAccessible(true);
				ReflectionUtils.setField(field, instanceInfo, instanceId);
			}
			{
				Field field = ReflectionUtils.findField(InstanceInfo.class, "hostName");
				field.setAccessible(true);
				ReflectionUtils.setField(field, instanceInfo, hostName);
			}
		}
	}

}
