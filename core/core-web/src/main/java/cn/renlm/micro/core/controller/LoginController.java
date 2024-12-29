package cn.renlm.micro.core.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.micro.core.dto.UserClaim;
import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.util.SessionUtil;
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
		return userDetails.toClaim();
	}

}
