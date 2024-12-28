package cn.renlm.micro.core.rbac.service;

import cn.renlm.micro.core.model.rbac.UserInfo;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2024-12-22
 */
public interface UserService {

	/**
	 * 根据登录账号获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	UserInfo loadUserByUsername(String username);

}
