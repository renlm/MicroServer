package cn.renlm.micro;

import static cn.renlm.micro.eureka.EurekaServerAuthConfig.CLIENT_AUTHORITY;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import cn.renlm.micro.eureka.EurekaServerAuthConfig.EurekaServerAuthFilter;

/**
 * 注册中心
 * 
 * @author RenLiMing(任黎明)
 *
 */
@EnableWebSecurity
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}

	@Bean
	CsrfTokenRepository csrfTokenRepository() {
		return new HttpSessionCsrfTokenRepository();
	}

	@Bean
	// @formatter:off
	SecurityFilterChain securityFilterChain(HttpSecurity http, EurekaServerAuthFilter authFilter) throws Exception {
		String[] anonymousRequests = { "/actuator/**" };
		http.authorizeHttpRequests(r -> {
			r.requestMatchers(anonymousRequests).permitAll()
			.requestMatchers("/eureka/**").hasAuthority(CLIENT_AUTHORITY)
			.anyRequest().authenticated()
			;
		});
		http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository()));
		http.addFilterBefore(authFilter, CsrfFilter.class);
		http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		return http.build();
		// @formatter:on
	}

}
