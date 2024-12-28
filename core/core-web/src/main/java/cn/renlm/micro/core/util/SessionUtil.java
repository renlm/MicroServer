package cn.renlm.micro.core.util;

import org.springframework.util.StringUtils;

import com.anji.captcha.util.AESUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;

/**
 * 会话工具
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class SessionUtil {

	public static final String AES_KEY = "AES-KEY";

	/**
	 * 获取AES加密串
	 * 
	 * @param request
	 * @return
	 */
	public static final String getAesKey(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String aesKey = (String) session.getAttribute(AES_KEY);
		if (StringUtils.hasText(aesKey)) {
			aesKey = AESUtil.getKey();
			session.setAttribute(AES_KEY, aesKey);
		}
		{
			return aesKey;
		}
	}

}
