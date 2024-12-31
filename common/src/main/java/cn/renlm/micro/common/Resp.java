package cn.renlm.micro.common;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

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

	private HttpStatus code;

	private String msg;

	private T data;

	/**
	 * 构建响应数据
	 * 
	 * @param <V>
	 * @param code
	 * @param msg
	 * @param data
	 * @return
	 */
	public static final <V> Resp<V> of(HttpStatus code, String msg, V data) {
		Resp<V> responseModel = new Resp<>();
		responseModel.setCode(code);
		responseModel.setMsg(msg);
		responseModel.setData(data);
		return responseModel;
	}

	/**
	 * 成功
	 * 
	 * @param <V>
	 * @param data
	 * @return
	 */
	public static final <V> Resp<V> success(V data) {
		Resp<V> responseModel = new Resp<>();
		responseModel.setCode(HttpStatus.OK);
		responseModel.setData(data);
		return responseModel;
	}

	/**
	 * 成功
	 * 
	 * @param <V>
	 * @param data
	 * @param msg
	 * @return
	 */
	public static final <V> Resp<V> success(V data, String msg) {
		Resp<V> responseModel = success(data);
		responseModel.setMsg(msg);
		return responseModel;
	}

	/**
	 * 错误
	 * 
	 * @param <V>
	 * @param code
	 * @param msg
	 * @return
	 */
	public static final <V> Resp<V> error(HttpStatus code, String msg) {
		Resp<V> responseModel = new Resp<>();
		responseModel.setCode(code);
		responseModel.setMsg(msg);
		return responseModel;
	}

}
