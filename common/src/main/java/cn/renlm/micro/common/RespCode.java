package cn.renlm.micro.common;

import lombok.AllArgsConstructor;

/**
 * 响应码
 * 
 * @author RenLiMing(任黎明)
 *
 */
@AllArgsConstructor
public enum RespCode {

	// @formatter:off
	OK(200, "成功"), 
	ERR(500, "失败"),
	UNAUTHORIZED(401, "用户未登录")
	;
	// @formatter:on

	public final int code;

	public final String desc;

}
