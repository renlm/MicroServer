package cn.renlm.micro.core.rbac.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cn.renlm.micro.core.model.rbac.UserInfoDto;
import cn.renlm.micro.core.rbac.entity.User;
import cn.renlm.micro.core.rbac.repository.UserRepository;
import cn.renlm.micro.core.rbac.service.UserService;
import jakarta.annotation.Resource;

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

	@Resource
	private UserRepository userRepository;

	@Override
	public UserInfoDto loadUserByUsername(String username) {
		UserInfoDto dto = new UserInfoDto();
		User user = userRepository.findByUsername(username);
		BeanUtils.copyProperties(user, dto);
		return dto;
	}

}
