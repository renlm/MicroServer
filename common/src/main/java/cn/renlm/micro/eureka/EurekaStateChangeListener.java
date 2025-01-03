package cn.renlm.micro.eureka;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.server.ReplicationClientAdditionalFilters;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.netflix.appinfo.InstanceInfo;

import cn.renlm.micro.properties.EurekaAuthProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Eureka 事件
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Component
@ConditionalOnMissingBean({ ReplicationClientAdditionalFilters.class })
public class EurekaStateChangeListener {
	
	@Resource
	private EurekaAuthProperties env;
    
    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        log.debug("服务注册事件：{}", instanceInfo.getHostName());
        this.update(instanceInfo);
    }
 
    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        log.debug("服务续约事件：{}", instanceInfo.getHostName());
        this.update(instanceInfo);
    }
    
    private void update (InstanceInfo instanceInfo) {
    	String hostName = instanceInfo.getHostName();
        int securePort = instanceInfo.getSecurePort();
        String instanceId = instanceInfo.getInstanceId();
        String podIp = env.getPodIp();
        if (env.isHeadless() && hostName.startsWith(podIp)) {
        	hostName = StringUtils.replace(hostName, podIp, StringUtils.replace(podIp, ".", "-"));
        	{
        		Field field = ReflectionUtils.findField(InstanceInfo.class, "hostName");
    			field.setAccessible(true);
    			ReflectionUtils.setField(field, instanceInfo, hostName);
        	}
        	{
        		instanceId = hostName + (securePort == 80 ? StringUtils.EMPTY : (":"+String.valueOf(securePort)));
        		Field field = ReflectionUtils.findField(InstanceInfo.class, "instanceId");
    			field.setAccessible(true);
    			ReflectionUtils.setField(field, instanceInfo, instanceId);
        	}
        }
    }
    
}
