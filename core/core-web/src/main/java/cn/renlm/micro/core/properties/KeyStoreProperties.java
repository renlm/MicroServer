package cn.renlm.micro.core.properties;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;

import com.nimbusds.jose.jwk.RSAKey;

import lombok.Data;

/**
 * 秘钥信息
 * keytool -genkeypair -alias alias -keyalg RSA -dname "C=CN" -keypass keypass -keystore keyStore.jks -storepass storepass
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Configuration
@ConfigurationProperties("encrypt.key-store")
public class KeyStoreProperties {

	private Resource location;

	private String alias;

	private String password;

	public RSAKey getRSAKey() {
		KeyStoreKeyFactory factory = new KeyStoreKeyFactory(location, password.toCharArray());
		KeyPair keyPair = factory.getKeyPair(alias);
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(alias).build();
	}

}
