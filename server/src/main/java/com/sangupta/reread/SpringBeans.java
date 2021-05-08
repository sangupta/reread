package com.sangupta.reread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.redislabs.modules.rejson.JReJSON;
import com.sangupta.reread.service.UserSettingsService;
import com.sangupta.reread.service.impl.RedisUserSettingsServiceImpl;

@Configuration
public class SpringBeans {
	
	public final String REDIS_HOST = "localhost";
	public final int REDIS_PORT = 6379;
	
	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}
	
	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(this.redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
	}
	
	@Bean
	public JReJSON jreJSON() {
		return new JReJSON(REDIS_HOST, REDIS_PORT);
	}

	@Bean
	public UserSettingsService userSettingsService() {
		return new RedisUserSettingsServiceImpl();
	}
	
}
