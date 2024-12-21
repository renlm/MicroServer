package cn.renlm.micro.core;

/**
 * 配置中心
 * 
 * @author RenLiMing(任黎明)
 *
 */
@EnableWebSecurity
@EnableConfigServer
@SpringBootApplication
public class CoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigApplication.class, args);
	}

}
