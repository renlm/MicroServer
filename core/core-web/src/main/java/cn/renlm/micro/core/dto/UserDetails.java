package cn.renlm.micro.core.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;

import cn.renlm.micro.core.model.rbac.UserInfo;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class UserDetails extends UserInfo implements org.springframework.security.core.userdetails.UserDetails {

	private static final long serialVersionUID = 1L;

	public static final UserDetails of(UserInfo userInfo) {
		UserDetails userDetails = new UserDetails();
		BeanUtils.copyProperties(userInfo, userDetails);
		return userDetails;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

}
