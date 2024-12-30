package cn.renlm.micro.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import com.anji.captcha.util.AESUtil;

import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserClaim;
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

	public static final String AES_KEY = "aesKey";

	/**
	 * 获取AES加密串
	 * 
	 * @param request
	 * @return
	 */
	public static final String getAesKey(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String aesKey = (String) session.getAttribute(AES_KEY);
		if (!StringUtils.hasText(aesKey)) {
			aesKey = AESUtil.getKey();
			session.setAttribute(AES_KEY, aesKey);
		}
		{
			return aesKey;
		}
	}
	
	/**
	 * 获取当前登录用户信息
	 * 
	 * @return
	 */
	public static final UserClaim getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.toClaim();
	}

}
