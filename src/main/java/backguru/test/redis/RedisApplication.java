package backguru.test.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class RedisApplication {
	@Bean
	public RedisTemplate<String, User> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();
		
		redisTemplate.setConnectionFactory(factory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(User.class));
		return redisTemplate;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
	}

}
