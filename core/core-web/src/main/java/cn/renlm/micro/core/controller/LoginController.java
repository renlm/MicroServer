package cn.renlm.micro.core.controller;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.core.dto.UserClaim;
import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
import cn.renlm.micro.core.util.SessionUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Resource
	private ApplicationContext applicationContext;

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	@Resource
	private UserClient userClient;

	/**
	 * 登录页
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/login")
	public String login(HttpServletRequest request, ModelMap model) {
		String aesKey = SessionUtil.getAesKey(request);
		model.put(SessionUtil.AES_KEY, aesKey);
		return "login";
	}

	/**
	 * 获取当前登录用户信息
	 * 
	 * @param authentication
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getCurrentUser")
	public UserClaim getCurrentUser(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		UserClaim userClaim = userDetails.toClaim();
		String username = userDetails.getUsername();
		{
			Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
			String serviceName = applicationContext.getId();
			String instanceId = eurekaInstanceConfig.getInstanceId();
			String hint = metadataMap.get("hint");
			logger.info("=== {} - username: {}, instanceId: {}, hint: {}", serviceName, username, instanceId, hint);
			UserInfo userInfo = userClient.loadUserByUsername(username);
			{
				userClaim.setAuthorities(new ArrayList<>());
				userClaim.getAuthorities().add(new SimpleGrantedAuthority(userInfo.getRemark()));
				userClaim.getAuthorities().add(new SimpleGrantedAuthority(serviceName + "-" + hint));
			}
		}
		{
			return userClaim;
		}
	}

}
