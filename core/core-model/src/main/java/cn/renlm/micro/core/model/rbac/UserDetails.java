package cn.renlm.micro.core.model.rbac;

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
	 * 用户Id
	 */
	private String userId;

	/**
	 * 登录账号
	 */
	private String userName;

}
