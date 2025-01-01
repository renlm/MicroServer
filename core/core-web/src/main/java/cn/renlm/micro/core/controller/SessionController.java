package cn.renlm.micro.core.controller;

import static cn.renlm.micro.constant.Constants.HINT_METADATA_NAME;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import cn.renlm.micro.common.Resp;
import cn.renlm.micro.common.RespCode;
import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserClaim;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
import cn.renlm.micro.core.security.UserDetailsService;
import cn.renlm.micro.core.util.SessionUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 会话
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/session")
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

	@Resource
	private UserDetailsService userDetailsService;

	/**
	 * 获取当前登录用户信息
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getCurrentUser")
	public Resp<UserClaim> getCurrentUser() {
		UserClaim userClaim = SessionUtil.getCurrentClaim();
		if (Objects.isNull(userClaim)) {
			return Resp.err(RespCode.UNAUTHORIZED);
		}
		{
			return Resp.ok(userClaim);
		}
	}

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getUserInfo")
	public Resp<UserClaim> getUserInfo(String userId) {
		Resp<UserInfo> resp = userClient.getByUserId(userId);
		UserInfo userInfo = resp.getData();
		if (Objects.isNull(userInfo)) {
			return Resp.err(RespCode.NOT_FOUND);
		}
		{
			Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
			String serviceName = applicationContext.getId();
			String instanceId = eurekaInstanceConfig.getInstanceId();
			String hint = metadataMap.get(HINT_METADATA_NAME);
			String username = userInfo.getUsername();
			String msg = serviceName + "/" + instanceId + "/" + hint;
			logger.info("=== username/serviceName/instanceId/hint: {}/{}", username, msg);
			{ // 备注信息
				List<GrantedAuthority> list = new ArrayList<>();
				list.add(new SimpleGrantedAuthority(resp.getMsg()));
				list.add(new SimpleGrantedAuthority(msg));
				UserClaim userClaim = UserDetails.of(userInfo).toClaim();
				userClaim.setAuthorities(list);
				return Resp.ok(userClaim);
			}
		}
	}

	/**
	 * 获取服务负载均衡标记
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getAllHints")
	public Resp<Set<String>> getAllHints() {
		Set<String> hints = new HashSet<>();
		Applications applications = eurekaClient.getApplications();
		List<Application> list = applications.getRegisteredApplications();
		for (Application app : list) {
			List<InstanceInfo> instances = app.getInstances();
			if (Objects.nonNull(instances)) {
				for (InstanceInfo instance : instances) {
					Map<String, String> metadataMap = instance.getMetadata();
					String hint = metadataMap.get(HINT_METADATA_NAME);
					if (StringUtils.hasText(hint)) {
						hints.add(hint);
					}
				}
			}
		}
		{
			return Resp.ok(hints);
		}
	}

	/**
	 * 更新会话负载均衡标记
	 * 
	 * @param request
	 * @param response
	 * @param hint
	 * @return
	 */
	@ResponseBody
	@PostMapping("/updateHint")
	public Resp<UserClaim> updateHint(HttpServletRequest request, HttpServletResponse response, String hint) {
		UserDetails info = userDetailsService.updateCurrentUser(request, response, user -> user.setHint(hint));
		return Resp.ok(info.toClaim());
	}

}
