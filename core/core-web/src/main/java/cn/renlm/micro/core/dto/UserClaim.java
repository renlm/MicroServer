package cn.renlm.micro.core.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class UserClaim implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 账号
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 真实姓名
	 */
	private String realname;

	/**
	 * 出生日期
	 */
	private LocalDate birthday;

	/**
	 * 性别，M：男，F：女
	 */
	private String sex;

	/**
	 * 手机号码
	 */
	private String mobile;

	/**
	 * 邮箱地址
	 */
	private String email;

	/**
	 * 权限列表
	 */
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
	private List<GrantedAuthority> authorities;

}
