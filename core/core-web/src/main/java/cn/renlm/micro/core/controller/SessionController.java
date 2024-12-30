package cn.renlm.micro.core.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import cn.renlm.micro.core.model.rbac.UserClaim;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
import cn.renlm.micro.core.util.SessionUtil;
import jakarta.annotation.Resource;

/**
 * 会话
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping
public class SessionController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Resource
	private ApplicationContext applicationContext;

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	@Resource
	private EurekaClient eurekaClient;

	@Resource
	private UserClient userClient;

	/**
	 * 获取当前登录用户信息
	 * 
	 * @param authentication
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getCurrentUser")
	public UserClaim getCurrentUser() {
		UserClaim userClaim = SessionUtil.getCurrentUser();
		String username = userClaim.getUsername();
		{
			Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
			String serviceName = applicationContext.getId();
			String instanceId = eurekaInstanceConfig.getInstanceId();
			String hint = metadataMap.get("hint");
			logger.info("=== {} - username: {}, instanceId: {}, hint: {}", serviceName, username, instanceId, hint);
			UserInfo userInfo = userClient.loadUserByUsername(username);
			{ // 备注信息
				List<GrantedAuthority> list = new ArrayList<>();
				list.add(new SimpleGrantedAuthority(userInfo.getRemark()));
				list.add(new SimpleGrantedAuthority(serviceName + "/" + instanceId + "/" + hint));
				userClaim.setAuthorities(list);
			}
		}
		{
			return userClaim;
		}
	}

	/**
	 * 获取服务负载均衡标记
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getAllHints")
	public Set<String> getAllHints() {
		Set<String> hints = new HashSet<>();
		Applications applications = eurekaClient.getApplications();
		List<Application> list = applications.getRegisteredApplications();
		for (Application app : list) {
			List<InstanceInfo> instances = app.getInstances();
			if (Objects.nonNull(instances)) {
				for (InstanceInfo instance : instances) {
					Map<String, String> metadataMap = instance.getMetadata();
					hints.add(metadataMap.get("hint"));
				}
			}
		}
		{
			return hints;
		}
	}

}
