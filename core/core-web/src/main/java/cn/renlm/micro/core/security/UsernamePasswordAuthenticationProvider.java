package cn.renlm.micro.core.security;

import java.lang.reflect.Field;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.ReflectionUtils;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.util.AESUtil;

/**
 * 登录认证检查
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class UsernamePasswordAuthenticationProvider extends DaoAuthenticationProvider {

	private final CaptchaService captchaService;

	public UsernamePasswordAuthenticationProvider(UserDetailsService userDetailsService,
			CaptchaService captchaService) {
		this.setUserDetailsService(userDetailsService);
		this.captchaService = captchaService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object details = authentication.getDetails();
		if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) details;
			CaptchaVO data = new CaptchaVO();
			data.setCaptchaVerification(webAuthenticationDetails.captchaVerification);
			try {
				ResponseModel verification = captchaService.verification(data);
				if (!verification.isSuccess()) {
					throw new SessionAuthenticationException("验证失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SessionAuthenticationException("验证失败");
			}
		}
		Authentication authen = super.authenticate(authentication);
		return authen;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		Object details = authentication.getDetails();
		if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) details;
			try {
				String credentials = authentication.getCredentials().toString();
				String decryptCredentials = AESUtil.aesDecrypt(credentials, webAuthenticationDetails.secretKey);
				Field field = ReflectionUtils.findField(UsernamePasswordAuthenticationToken.class, "credentials");
				field.setAccessible(true);
				ReflectionUtils.setField(field, authentication, decryptCredentials);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BadCredentialsException("用户名或密码错误");
			}
		}
		{
			super.additionalAuthenticationChecks(userDetails, authentication);
		}
	}

}
