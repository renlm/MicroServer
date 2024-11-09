package cn.renlm.GrmServer;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import cn.renlm.GrmServer.aot.hint.MyRuntimeHints;

/**
 * 配置中心
 * 
 * @author RenLiMing(任黎明)
 *
 */
@EnableWebSecurity
@EnableConfigServer
@SpringBootApplication
@ImportRuntimeHints({ MyRuntimeHints.class })
public class ConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigApplication.class, args);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		String[] anonymousRequests = { "/actuator/**" };
		http.authorizeHttpRequests(r -> r.requestMatchers(anonymousRequests).permitAll().anyRequest().authenticated());
		http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		http.csrf(csrf -> csrf.disable());
		return http.build();
	}

}
