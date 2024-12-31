package cn.renlm.micro.core.controller;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
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
import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserClaim;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
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

	@Resource
	private SecurityContextRepository securityContextRepository;

	/**
	 * 获取当前登录用户信息
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getCurrentUser")
	public Resp<UserClaim> getCurrentUser() {
		UserClaim userClaim = SessionUtil.getCurrentUser();
		String username = userClaim.getUsername();
		{
			Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
			String serviceName = applicationContext.getId();
			String instanceId = eurekaInstanceConfig.getInstanceId();
			String hint = metadataMap.get("hint");
			logger.info("=== {} - username: {}, instanceId: {}, hint: {}", serviceName, username, instanceId, hint);
			Resp<UserInfo> resp = userClient.loadUserByUsername(username);
			{ // 备注信息
				UserInfo userInfo = resp.getData();
				List<GrantedAuthority> list = new ArrayList<>();
				list.add(new SimpleGrantedAuthority(userInfo.getRemark()));
				list.add(new SimpleGrantedAuthority(serviceName + "/" + instanceId + "/" + hint));
				userClaim.setAuthorities(list);
			}
		}
		{
			return Resp.success(userClaim);
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
					String hint = metadataMap.get("hint");
					if (StringUtils.hasText(hint)) {
						hints.add(hint);
					}
				}
			}
		}
		{
			return Resp.success(hints);
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
		SecurityContext context = SecurityContextHolder.getContext();
		UserDetails principal = (UserDetails) context.getAuthentication();
		Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
		principal.setHint(hint);
		context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, EMPTY, authorities));
		securityContextRepository.saveContext(context, request, response);
		return Resp.success(principal.toClaim());
	}

}
