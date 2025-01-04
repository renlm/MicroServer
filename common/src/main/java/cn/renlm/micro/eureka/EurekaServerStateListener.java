package cn.renlm.micro.eureka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 服务端事件
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Component
@ConditionalOnClass(name = "org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent")
public class EurekaServerStateListener {

	@EventListener
	public void listen(EurekaServerStartedEvent event) {
		log.debug("服务端启动...");
	}

	@EventListener
	public void listen(EurekaRegistryAvailableEvent event) {
		log.debug("服务端可用");
	}

	@EventListener
	public void listen(EurekaInstanceRegisteredEvent event) {
		InstanceInfo instanceInfo = event.getInstanceInfo();
		String appName = instanceInfo.getAppName();
		String hostName = instanceInfo.getHostName();
		log.debug("客户端服务注册 - appName: {}, hostName: {}", appName, hostName);
	}

	@EventListener
	public void listen(EurekaInstanceRenewedEvent event) {
		InstanceInfo instanceInfo = event.getInstanceInfo();
		String appName = instanceInfo.getAppName();
		String hostName = instanceInfo.getHostName();
		log.debug("客户端服务续约 - appName: {}, hostName: {}", appName, hostName);
	}

	@EventListener
	public void listen(EurekaInstanceCanceledEvent event) {
		String appName = event.getAppName();
		String serverId = event.getServerId();
		log.debug("客户端服务下线 - appName: {}, serverId: {}", appName, serverId);
	}

}
