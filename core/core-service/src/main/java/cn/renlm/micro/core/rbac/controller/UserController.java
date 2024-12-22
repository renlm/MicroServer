package cn.renlm.micro.core.rbac.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@Resource
	private UserService userService;

	/**
	 * 根据登录账号获取用户信息
	 * 
	 * @param userName
	 * @return
	 */
	@ResponseBody
	@GetMapping("/loadUserByUserName")
	public Object loadUserByUserName(String userName) {
		return null;
	}

}
