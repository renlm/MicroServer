package cn.renlm.micro.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;

import cn.renlm.micro.core.properties.AjCaptchaProperties;

@Configuration
public class AjCaptchaStorageAutoConfiguration {

    @Bean(name = "AjCaptchaCacheService")
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties ajCaptchaProperties){
        return CaptchaServiceFactory.getCache(ajCaptchaProperties.getCacheType().name());
    }
}
