package cn.renlm.micro.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
