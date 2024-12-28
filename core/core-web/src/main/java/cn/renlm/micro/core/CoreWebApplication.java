package cn.renlm.micro.core;

import java.util.Arrays;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.thymeleaf.expression.Lists;

import cn.renlm.micro.core.web.AuthorizationConsentController;

/**
 * CoreWeb
 * 
 * @author RenLiMing(任黎明)
 *
 */
@SpringBootApplication
@ImportRuntimeHints(CoreWebApplication.DemoAuthorizationServerApplicationRuntimeHintsRegistrar.class)
public class CoreWebApplication {
	
	static class DemoAuthorizationServerApplicationRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			// Thymeleaf
			hints.reflection().registerTypes(
					Arrays.asList(
							TypeReference.of(AuthorizationConsentController.ScopeWithDescription.class),
							TypeReference.of(Lists.class)
					), builder ->
							builder.withMembers(MemberCategory.DECLARED_FIELDS,
									MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS)
			);
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(CoreWebApplication.class, args);
	}

}
