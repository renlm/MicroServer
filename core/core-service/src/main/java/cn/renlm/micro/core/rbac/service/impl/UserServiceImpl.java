package cn.renlm.micro.core.rbac.service.impl;

import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cn.renlm.micro.core.model.rbac.UserInfo;
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
	public UserInfo loadUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if (Objects.isNull(user)) {
			return null;
		} else {
			UserInfo dto = new UserInfo();
			BeanUtils.copyProperties(user, dto);
			return dto;
		}
	}

	@Override
	public UserInfo findByUserId(String userId) {
		User user = userRepository.findByUserId(userId);
		if (Objects.isNull(user)) {
			return null;
		} else {
			UserInfo dto = new UserInfo();
			BeanUtils.copyProperties(user, dto);
			return dto;
		}
	}

}
