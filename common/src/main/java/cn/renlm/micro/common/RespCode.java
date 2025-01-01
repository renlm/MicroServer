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
	UNAUTHORIZED(401, "未登录"),
	NOT_FOUND(404, "未找到数据")
	;
	// @formatter:on

	public final int code;

	public final String desc;

}
