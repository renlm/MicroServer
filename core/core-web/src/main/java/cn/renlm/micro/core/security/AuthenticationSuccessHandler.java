package cn.renlm.micro.core.security;

import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 登录成功处理
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

	@Lazy
	@Resource
	private UserDetailsService userDetailsService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		userDetailsService.updateCurrentUser(request, response, principal -> {
			String hintHeaderName = userDetailsService.getDefaultHintHeaderName();
			Cookie[] cookies = request.getCookies();
			if (Objects.nonNull(cookies)) {
				for (Cookie cookie : cookies) {
					if (hintHeaderName.equals(cookie.getName()) && hasText(cookie.getValue())) {
						principal.setHint(cookie.getValue());
						break;
					}
				}
			}
			{
				log.debug("登录成功 - username: {}, hint: {}", principal.getUsername(), principal.getHint());
			}
		});
		{
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}

}
