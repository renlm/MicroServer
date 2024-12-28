package cn.renlm.micro.core.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.anji.captcha.service.CaptchaCacheService;

import jakarta.annotation.Resource;

/**
 * CaptchaCacheService SPI 实现类
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class CaptchaRedisCacheService implements CaptchaCacheService {

	private static final String TYPE = "redis";

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void set(String key, String value, long expiresInSeconds) {
		stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
	}

	@Override
	public boolean exists(String key) {
		return stringRedisTemplate.hasKey(key);
	}

	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

	@Override
	public String get(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public Long increment(String key, long val) {
		return stringRedisTemplate.opsForValue().increment(key, val);
	}

}
