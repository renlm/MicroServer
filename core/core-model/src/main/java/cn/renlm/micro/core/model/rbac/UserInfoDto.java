package cn.renlm.micro.core.model.rbac;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
public class UserInfoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 账号
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

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
	private Date birthday;

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
	 * 是否未过期（账号）
	 */
	boolean accountNonExpired;

	/**
	 * 是否未锁定（账号）
	 */
	boolean accountNonLocked;

	/**
	 * 是否未过期（账号）
	 */
	boolean credentialsNonExpired;

	/**
	 * 是否启用
	 */
	boolean enabled;

}
