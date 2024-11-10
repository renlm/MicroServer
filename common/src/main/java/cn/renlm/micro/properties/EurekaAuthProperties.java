package cn.renlm.micro.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 注册中心认证
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ConfigurationProperties("eureka.auth")
public class EurekaAuthProperties {

	private String secretKey;

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
