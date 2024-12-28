package cn.renlm.micro.core.security;

import org.springframework.stereotype.Service;

import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
import jakarta.annotation.Resource;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Resource
	private UserClient userClient;

	@Override
	public UserDetails loadUserByUsername(String username) {
		UserInfo userInfo = userClient.loadUserByUsername(username);
		return UserDetails.of(userInfo);
	}

}
