package cn.renlm.micro.util;

import java.util.Objects;

import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

import lombok.experimental.UtilityClass;

/**
 * 在线程中保存请求头信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class OpenFeignHeadersHolder {

	private static final String NAME = "OpenFeign Request headers";

	private static final ThreadLocal<HttpHeaders> HOLDER = new NamedThreadLocal<>(NAME);

	public static final void reset() {
		HOLDER.remove();
	}

	public static HttpHeaders get() {
		return HOLDER.get();
	}

	public static final void set(@Nullable HttpHeaders httpHeaders) {
		if (Objects.isNull(httpHeaders)) {
			reset();
		} else {
			HOLDER.set(httpHeaders);
		}
	}

}
