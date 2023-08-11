package backguru.test.redis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import backguru.test.redis.User;

@SpringBootTest
class RedisApplicationTests {
	@Autowired
	private RedisTemplate<String, User> redisTemplate;
	
	private final String redisKey = "tony";
	private final String name = "tonycho";
	private final int age = 48;
	
	User user = new User(name, age);
	
	@DisplayName("Redis Write: Jackson2JsonRedisSerializer")
	@Test
	public void putRedis() {
		redisTemplate.opsForValue().set(redisKey, user);
	}
	
	@DisplayName("Redis Read: Jackson2JsonRedisSerializer")
	@Test
	public void getRedis() {
		User user = redisTemplate.opsForValue().get(redisKey);
		assertThat(user.getName()).isEqualTo(name);
		assertThat(user.getAge()).isEqualTo(age);
		
		System.out.println("Name:" + user.getName() + ", age:" + user.getAge());
	}
}
