package cn.renlm.micro.core.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import cn.renlm.micro.core.model.rbac.UserInfoDto;
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
	private SecurityContextRepository securityContextRepository;

	@Resource
	private UserClient userClient;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfoDto userInfo = userClient.loadUserByUserName(username);
		return new UserDetails() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getUsername() {
				return userInfo.getUsername();
			}
			
			@Override
			public String getPassword() {
				return userInfo.getPassword();
			}
			
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
		};
	}

}
