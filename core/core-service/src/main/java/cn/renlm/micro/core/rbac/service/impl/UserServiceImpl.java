package cn.renlm.micro.core.rbac.service.impl;

import org.springframework.stereotype.Service;

import cn.renlm.micro.core.model.rbac.UserDetails;
import cn.renlm.micro.core.rbac.service.UserService;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2024-12-22
 */
@Service
public class UserServiceImpl implements UserService {

	@Override
	public UserDetails loadUserByUserName(String userName) {
		return null;
	}

}
