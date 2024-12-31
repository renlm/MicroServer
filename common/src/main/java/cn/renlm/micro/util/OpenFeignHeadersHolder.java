package cn.renlm.micro.util;

import java.util.Objects;

import org.springframework.core.NamedInheritableThreadLocal;
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

	private static final String I_NAME = "OpenFeign Request Inheritable headers";

	private static final ThreadLocal<HttpHeaders> HOLDER = new NamedThreadLocal<>(NAME);

	private static final ThreadLocal<HttpHeaders> I_HOLDER = new NamedInheritableThreadLocal<>(I_NAME);

	public static final void reset() {
		HOLDER.remove();
		I_HOLDER.remove();
	}

	public static HttpHeaders get() {
		HttpHeaders httpHeaders = HOLDER.get();
		if (Objects.isNull(httpHeaders)) {
			return I_HOLDER.get();
		} else {
			return httpHeaders;
		}
	}

	public static void set(@Nullable HttpHeaders httpHeaders) {
		set(httpHeaders, false);
	}

	public static final void set(@Nullable HttpHeaders httpHeaders, boolean inheritable) {
		if (Objects.isNull(httpHeaders)) {
			reset();
		} else {
			if (inheritable) {
				I_HOLDER.set(httpHeaders);
				HOLDER.remove();
			} else {
				HOLDER.set(httpHeaders);
				I_HOLDER.remove();
			}
		}
	}

}
