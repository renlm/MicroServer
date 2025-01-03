package cn.renlm.micro.eureka;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.server.ReplicationClientAdditionalFilters;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.netflix.appinfo.InstanceInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 事件
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Component
@ConditionalOnClass({ ReplicationClientAdditionalFilters.class })
public class EurekaStateChangeListener {

	@EventListener
	public void listen(EurekaInstanceRegisteredEvent event) {
		InstanceInfo instanceInfo = event.getInstanceInfo();
		this.update("服务注册事件", instanceInfo);
	}

	@EventListener
	public void listen(EurekaInstanceRenewedEvent event) {
		InstanceInfo instanceInfo = event.getInstanceInfo();
		this.update("服务续约事件", instanceInfo);
	}

	private void update(String event, InstanceInfo instanceInfo) {
		String appName = instanceInfo.getAppName();
		String podIp = instanceInfo.getHostName();
		String podName = StringUtils.replace(podIp, ".", "-");
		String securePort = String.valueOf(instanceInfo.getSecurePort());
		String instanceId = instanceInfo.getInstanceId();
		{
			instanceId = StringUtils.replace(instanceId, podIp, podName);
			String hostName = instanceId.substring(0, instanceId.indexOf(":" + securePort));
			log.debug("{} - appName: {}, hostName: {}, instanceId: {}", appName, hostName, instanceId);
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
