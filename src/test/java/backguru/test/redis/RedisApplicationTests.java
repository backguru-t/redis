package backguru.test.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisApplicationTests {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RedisConnectionFactory connectionFactory;
	@Autowired
	private ObjectMapper objectMapper;

	private final String redisKey = "tony";
	private final String name = "tonycho";
	private final int age = 48;
	
	User user = new User(name, age);
	
	@DisplayName("Redis Write: Jackson2JsonRedisSerializer")
	@Test
	public void putRedis() throws JsonProcessingException {
		redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(user));
	}
	
	@DisplayName("Redis Read: Jackson2JsonRedisSerializer")
	@Test
	public void getRedis() throws JsonProcessingException {
		String cached = (String)redisTemplate.opsForValue().get(redisKey);
		User user = objectMapper.readValue(cached, User.class);
		assertThat(user.getName()).isEqualTo(name);
		assertThat(user.getAge()).isEqualTo(age);

		System.out.println("Name:" + user.getName() + ", age:" + user.getAge());
		connectionFactory.getConnection().serverCommands().flushAll();
	}
}
