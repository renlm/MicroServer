package cn.renlm.micro.core.rbac.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2024-12-28
 */
@Data
@Entity
public class User {

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

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
