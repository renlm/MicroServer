package cn.renlm.micro.common;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

	@Getter
	private int value;

	@Getter
	private String msg;

	@JsonValue
	public String getJsonValue() {
		return this.name();
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

	/**
	 * 反序列化处理
	 */
	public static class Deserializer extends JsonDeserializer<RespCode> {

		@Override
		public RespCode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String name = p.getText();
			return ofName(name);
		}

	}

}
