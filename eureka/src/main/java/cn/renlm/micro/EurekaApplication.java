package cn.renlm.micro;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import cn.renlm.micro.filter.EurekaServerAuthFilter;

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
	SecurityFilterChain securityFilterChain(HttpSecurity http, EurekaServerAuthFilter authFilter) throws Exception {
		String[] anonymousRequests = { "/actuator/**" };
		http.authorizeHttpRequests(r -> r.requestMatchers(anonymousRequests).permitAll().anyRequest().authenticated());
		http.addFilterBefore(authFilter, BasicAuthenticationFilter.class);
		http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		http.csrf(csrf -> csrf.disable());
		return http.build();
	}

}
