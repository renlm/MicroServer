package cn.renlm.micro;

import java.util.Base64;
import java.util.Base64.Encoder;

import org.junit.jupiter.api.Test;

/**
 * BasicAuth
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class BasicAuthTest {

	Encoder encoder = Base64.getMimeEncoder();

	@Test
	public void test() {
		String username = "default";
		String password = "M1h62Gj3Uy54r";
		String src = username + ":" + password;
		String base64Credentials = new String(encoder.encode(src.getBytes()));
		System.out.println("Authorization: Basic " + base64Credentials);
	}

}
