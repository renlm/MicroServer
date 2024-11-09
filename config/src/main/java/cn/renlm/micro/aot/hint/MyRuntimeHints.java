package cn.renlm.GrmServer.aot.hint;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Native image
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class MyRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.resources().registerPattern("keyStore.jks");
	}

}
