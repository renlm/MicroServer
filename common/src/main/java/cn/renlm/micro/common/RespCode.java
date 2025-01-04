package cn.renlm.micro.common;

import com.fasterxml.jackson.annotation.JsonCreator;

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
	UNKNOWN(0, "未知"), 
	OK(200, "成功"), 
	ERR(500, "失败"),
	UNAUTHORIZED(401, "未登录"),
	NOT_FOUND(404, "未找到数据")
	;
	// @formatter:on

	public final int code;

	public final String desc;

	@JsonCreator
	public static RespCode ofCode(int code) {
		RespCode[] arr = RespCode.values();
		for (int i = 0; i < arr.length; i++) {
			RespCode rc = arr[i];
			if (rc.code == code) {
				return rc;
			}
		}
		{
			return UNKNOWN;
		}
	}

	@JsonCreator
	public static RespCode ofName(String name) {
		RespCode[] arr = RespCode.values();
		for (int i = 0; i < arr.length; i++) {
			RespCode rc = arr[i];
			if (rc.name().equals(name)) {
				return rc;
			}
		}
		{
			return UNKNOWN;
		}
	}

}
