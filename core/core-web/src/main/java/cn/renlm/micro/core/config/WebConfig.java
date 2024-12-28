package cn.renlm.micro.core.config;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.LiteWebJarsResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import jakarta.annotation.Resource;

/**
 * Web 配置
 *
 * @author RenLiMing(任黎明)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Resource
	private ThymeleafProperties thymeleafProperties;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new Converter<String, String>() {
			@Override
			public String convert(String source) {
				return StringUtils.hasText(source) ? source.stripLeading().stripTrailing() : source;
			}
		});
	}

	@Override
	// @formatter:off
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		VersionResourceResolver resolver = new VersionResourceResolver();
		resolver.addVersionStrategy(new ContentVersionStrategy(), "/**");

		registry.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/")
			.resourceChain(thymeleafProperties.isCache())
			.addResolver(resolver);

		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/")
			.resourceChain(thymeleafProperties.isCache())
			.addResolver(resolver)
			.addResolver(new LiteWebJarsResourceResolver())
			.addResolver(new PathResourceResolver());
	}
	// @formatter:on
}
