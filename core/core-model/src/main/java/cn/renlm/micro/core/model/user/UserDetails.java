package cn.renlm.micro.core.model.user;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
public class UserDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 登录账号
	 */
	private String userName;

}
