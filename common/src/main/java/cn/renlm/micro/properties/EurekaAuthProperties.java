package cn.renlm.micro.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@ConfigurationProperties("eureka.auth")
public class EurekaAuthProperties {

	private String secretKey;

}
