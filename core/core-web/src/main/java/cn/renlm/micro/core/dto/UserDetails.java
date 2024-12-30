package cn.renlm.micro.core.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.renlm.micro.core.model.rbac.UserClaim;
import cn.renlm.micro.core.model.rbac.UserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class UserDetails extends UserInfo implements org.springframework.security.core.userdetails.UserDetails {

	private static final long serialVersionUID = 1L;

	private String hint;

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
	private List<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	public final UserClaim toClaim() {
		UserClaim userClaim = new UserClaim();
		BeanUtils.copyProperties(this, userClaim);
		return userClaim;
	}

	public static final UserDetails of(UserInfo userInfo) {
		UserDetails userDetails = new UserDetails();
		BeanUtils.copyProperties(userInfo, userDetails);
		return userDetails;
	}

}
