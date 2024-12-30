package cn.renlm.micro.core.rbac.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.rbac.service.UserService;
import jakarta.annotation.Resource;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2024-12-22
 */
@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource
	private ApplicationContext applicationContext;

	@Resource
	private UserService userService;

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	/**
	 * 根据登录账号获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	@ResponseBody
	@GetMapping("/loadUserByUsername")
	public UserInfo loadUserByUsername(String username) {
		Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
		String serviceName = applicationContext.getId();
		String instanceId = eurekaInstanceConfig.getInstanceId();
		String hint = metadataMap.get("hint");
		logger.info("=== {} - username: {}, instanceId: {}, hint: {}", serviceName, username, instanceId, hint);
		UserInfo userInfo = userService.loadUserByUsername(username);
		userInfo.setRemark(serviceName + "/" + instanceId + "/" + hint);
		return userInfo;
	}

}
