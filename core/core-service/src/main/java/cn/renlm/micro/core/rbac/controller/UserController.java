package cn.renlm.micro.core.rbac.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.micro.core.model.rbac.UserDetails;
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
	private UserService userService;

	/**
	 * 根据登录账号获取用户信息
	 * 
	 * @param userName
	 * @return
	 */
	@ResponseBody
	@GetMapping("/loadUserByUserName")
	public UserDetails loadUserByUserName(String userName) {
		logger.info("根据登录账号获取用户信息");
		return userService.loadUserByUserName(userName);
	}

}
