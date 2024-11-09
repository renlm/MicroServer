package cn.renlm.micro.util;

import static java.util.UUID.randomUUID;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.codec.Utf8;

/**
 * Cross-Site Request Forgery
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class CsrfTokenUtil {

	/**
	 * 生成令牌（服务端）
	 * 
	 * @return
	 */
	public static final String createServerToken() {
		return randomUUID().toString();
	}

	/**
	 * 生成令牌（客户端）
	 * 
	 * @param secureRandom
	 * @param token
	 * @return
	 */
	public static final String createCsrfToken(SecureRandom secureRandom, String token) {
		byte[] csrfBytes = Utf8.encode(token);
		byte[] randomBytes = new byte[csrfBytes.length];
		secureRandom.nextBytes(randomBytes);

		int len = csrfBytes.length;
		byte[] xoredBytes = new byte[len];
		System.arraycopy(csrfBytes, 0, xoredBytes, 0, len);
		for (int i = 0; i < len; i++) {
			xoredBytes[i] ^= randomBytes[i];
		}

		byte[] combinedBytes = new byte[csrfBytes.length + randomBytes.length];
		System.arraycopy(randomBytes, 0, combinedBytes, 0, randomBytes.length);
		System.arraycopy(xoredBytes, 0, combinedBytes, randomBytes.length, xoredBytes.length);

		return Base64.getUrlEncoder().encodeToString(combinedBytes);
	}

}
