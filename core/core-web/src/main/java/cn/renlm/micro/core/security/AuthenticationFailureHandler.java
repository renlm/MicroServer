package cn.renlm.micro.core.security;

import static cn.renlm.micro.core.config.WebSecurityConfig.LoginPage;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录失败处理
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		super.setDefaultFailureUrl(LoginPage + "?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
