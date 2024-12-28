package cn.renlm.micro.core.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.renlm.micro.core.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录认证信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class WebAuthenticationDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	String secretKey;

	String captchaVerification;

	public WebAuthenticationDetails() {
	}

	public WebAuthenticationDetails(HttpServletRequest request) {
		this.secretKey = SessionUtil.getAesKey(request);
		this.captchaVerification = request.getParameter("captchaVerification");
	}

}
