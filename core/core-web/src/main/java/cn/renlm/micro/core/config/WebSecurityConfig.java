package cn.renlm.micro.core.config;

import java.util.Locale;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import com.anji.captcha.service.CaptchaService;

import cn.renlm.micro.core.security.AuthenticationFailureHandler;
import cn.renlm.micro.core.security.AuthenticationSuccessHandler;
import cn.renlm.micro.core.security.RequestAuthorizationManager;
import cn.renlm.micro.core.security.UserDetailsService;
import cn.renlm.micro.core.security.UsernamePasswordAuthenticationProvider;
import cn.renlm.micro.core.security.WebAuthenticationDetails;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Security 配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Configuration
@EnableWebSecurity
@EnableRedisIndexedHttpSession(redisNamespace = "micro:session", maxInactiveIntervalInSeconds = 60 * 60 * 12)
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

	/**
	 * 登录页
	 */
	public static final String LoginPage = "/login";

	/**
	 * 退出地址
	 */
	public static final String logoutUrl = "/logout";

	/**
	 * 登录接口
	 */
	public static final String LoginProcessingUrl = "/doLogin";

	/**
	 * 验证码路径
	 */
	public static final String CaptchaAntMatcher = "/captcha/**";

	/**
	 * 白名单
	 */
	// @formatter:off
	public static final String[] WHITE_LIST = { 
			LoginPage, 
			logoutUrl, 
			LoginProcessingUrl, 
			CaptchaAntMatcher,
			"/session/getCurrentUser",
			"/actuator/**" 
		};
		// @formatter:on

	/**
	 * 静态资源
	 */
	// @formatter:off
	public static final String[] STATIC_PATHS = { 
			"/favicon.ico", 
			"/static/**", 
			"/webjars/**"
		};
		// @formatter:on

	@Resource
	private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

	@Resource
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Resource
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Bean
	// @formatter:off
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestAuthorizationManager authorizationManager)
			throws Exception {
		// 显示保存 SecurityContext
		http.securityContext(securityContext -> {
			securityContext.requireExplicitSave(true);
			securityContext.securityContextRepository(securityContextRepository());
		});
		// 启用Csrf
		http.csrf(csrf -> {
			csrf.ignoringRequestMatchers(CaptchaAntMatcher)
				.ignoringRequestMatchers(WHITE_LIST)
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		});
		// 会话
		http.sessionManagement(sessionManagement -> {
			sessionManagement.invalidSessionUrl(LoginPage)
				.maximumSessions(1000)
				.expiredUrl(LoginPage)
				.sessionRegistry(sessionRegistry());
		});
		// 资源访问控制
		http.authorizeHttpRequests(authorizeHttpRequests -> {
			// 放行所有OPTIONS请求
			authorizeHttpRequests.requestMatchers(HttpMethod.OPTIONS).permitAll()
				// 白名单
				.requestMatchers(WHITE_LIST).permitAll()
				// 静态资源
				.requestMatchers(STATIC_PATHS).permitAll()
				// 请求访问限制
				.anyRequest().access(authorizationManager);
		});
		// Iframe同源访问
		http.headers(headers -> {
			headers.frameOptions(frameOptions -> {
				frameOptions.sameOrigin();
			});
		});
		// 登录
		http.formLogin(formLogin -> {
			formLogin.loginPage(LoginPage)
				.loginProcessingUrl(LoginProcessingUrl)
				.authenticationDetailsSource(authenticationDetailsSource())
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler);
		});
		// 注销
		http.logout(logout -> {
			logout.logoutUrl(logoutUrl)
				.invalidateHttpSession(true);
		});
		return http.build();
		// @formatter:on
	}

	@Bean
	// @formatter:off
	SecurityContextRepository securityContextRepository() {
		return new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
		// @formatter:on
	}

	/**
	 * 会话并发
	 * 
	 * @return
	 */
	@Bean
	SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}

	/**
	 * 附加信息
	 * 
	 * @return
	 */
	@Bean
	AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
		return new AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>() {
			@Override
			public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
				return new WebAuthenticationDetails(context);
			}
		};
	}

	@Bean
	public MessageSource messageSource() {
		Locale.setDefault(Locale.CHINA);
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.addBasenames(new String[] { "classpath:org/springframework/security/messages" });
		return messageSource;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return passwordEncoder;
	}

	@Bean
	// @formatter:off
	public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, CaptchaService captchaService) {
		UsernamePasswordAuthenticationProvider provider = new UsernamePasswordAuthenticationProvider(userDetailsService, captchaService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
		// @formatter:on
	}

}
