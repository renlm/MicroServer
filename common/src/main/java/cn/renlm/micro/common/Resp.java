package cn.renlm.micro.common;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 响应数据
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
public class Resp<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String code;

	private String msg;

	private T data;

	/**
	 * 响应是否成功
	 * 
	 * @return
	 */
	public boolean isOk() {
		return RespCode.OK.name().equals(code);
	}

	/**
	 * 构建响应数据
	 * 
	 * @param <V>
	 * @param code
	 * @param msg
	 * @param data
	 * @return
	 */
	public static final <V> Resp<V> of(RespCode code, String msg, V data) {
		Resp<V> resp = new Resp<>(code.name());
		resp.setMsg(msg);
		resp.setData(data);
		return resp;
	}

	/**
	 * 成功
	 * 
	 * @param <V>
	 * @param data
	 * @return
	 */
	public static final <V> Resp<V> ok(V data) {
		Resp<V> resp = new Resp<>(RespCode.OK.name());
		resp.setData(data);
		return resp;
	}

	/**
	 * 成功
	 * 
	 * @param <V>
	 * @param data
	 * @param msg
	 * @return
	 */
	public static final <V> Resp<V> ok(V data, String msg) {
		Resp<V> resp = ok(data);
		resp.setMsg(msg);
		return resp;
	}

	/**
	 * 错误
	 * 
	 * @param <V>
	 * @param code
	 * @return
	 */
	public static final <V> Resp<V> err(RespCode code) {
		Resp<V> resp = new Resp<>(code.name());
		return resp;
	}

	/**
	 * 错误
	 * 
	 * @param <V>
	 * @param code
	 * @param msg
	 * @return
	 */
	public static final <V> Resp<V> err(RespCode code, String msg) {
		Resp<V> resp = err(code);
		resp.setMsg(msg);
		return resp;
	}

}
