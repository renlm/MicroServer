package cn.renlm.micro.core.model.rbac;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
public class UserClaim implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 负载均衡标记
	 */
	private String hint;

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
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
	private List<?> authorities;

}
