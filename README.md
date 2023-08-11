# Redis Test
## RedisTemplate Bean 생성
RedisTemplate과 RedisConnectionFactory를 Bean으로 생성하기 위해 @SpringBootApplication이 달려 있는 메인 클래스에서 구현하였다.
@SpringBootApplication이 @Configuration 어노테이션을 포함하고 있기 때문이다. 그리고 실제로 구체적으로 redis 설정을 하기 위해 직접
RedisStandaloneConfiguration을 생성하여 LettuceConnectionFactory를 리턴하도록 했다.

## Serializer 설정
Redis 객체를 저장할 때는 serializer로 직렬화/역직렬화를 해 주어야 한다.

### GenericJackson2JsonRedisSerializer
이 Serializer는 객체의 클래스 지정없이 직렬화해준다는 장점을 가지고있다. 하지만, 단점으로는 Object의 Class 및 package까지 전부 함께 
저장하게 되어 다른 프로젝트에서 redis에 저장되어 있는 값을 사용하려면 package까지 일치시켜줘야하는 큰 단점이 존재한다.

따라서 MSA 관점의 프로젝트에서는 사용하지 않는 것이 좋고, 만약 프로젝트에 변경사항이 자주 발생한다면 그 때도 문제가 생길 수 있으니 사용을
추천하지 않는다.

### Jackson2JsonRedisSerializer
이 serializer는 클래스를 지정해야해서, redis에 객체를 저장할 때 class 값을 저장하지 않는다. 따라서, package 등이 일치할 필요가 없다는 
장점이 있다. 하지만, class 타입을 지정하기 때문에 redisTemplate을 여러 쓰레드에서 접근하게 될 때 serializer 타입의 문제가 발생하는 경우가 
발생한다. 또한, 항상 class type을 지정해 주어야 하기 때문에 DTO가 추가 될때 마다 따로따로 redistemplate을 생성해야 하는 단점이 있다.

### StringRedisSerializer
이름을 보면 알 수 있듯이, string 값을 그대로 저장하는 serilaizer이다. 이것을 사용하게 되면, JSON 형태로 직접 encoding, decoding을 
해줘야한다는 단점이 있지만 위의 두 개의 serializer에서 발생할 수 있는 문제가 발생하지 않는다. class 타입의 지정이 필요하지 않고 package까지 
일치할 필요가 없다. 그리고 쓰레드간의 문제가 발생하지 않는다. 위 3개의 장점이 JSON을 직접 파싱하는 것보다 더 이익이 크기 때문에, 
string redis serializer를 사용한다. 대신 Json으로 직접 파싱 해 주는 유틸리티를 만들어 사용한다.

## Json parsing
아래와 같이 Object -> JSON, JSON -> Object를 위한 util 클래스를 하나 만들어 두면, 편리하게 사용할 수 있다. 엔티티 자체를 JSON으로 
직렬화 할 때는 JPA를 사용할 시, 양방향 순환참조가 발생하여 stack overflow를 발생시키는 경우가 자주 있다. 이를 해결하기 위해서는 DTO를 
사용한다거나, @JsonIgnore를 사용한다.

```java
public <T> boolean saveData(String key, T data) {
	try {
      ObjectMapper mapper = new ObjectMapper();
      String value = objectMapper.writeValueAsString(data);
      redisTemplate.opsForValue().set(key, value);
      return true;
    } catch(Exception e){
    	log.error(e);
      	return false;
    }
}

public <T> Optional<T> getData(String key, Class<T> classType) {
	String value = redisTemplate.opsForValue().get(key);
    
    if(value == null){
    	return Optional.empty();
    }
    
	try {
    	ObjectMapper mapper = new ObjectMapper();
     	return Optional.of(objectMapper.readValue(value));
    } catch(Exception e){
    	log.error(e);
      	return Optional.empty();
    }
}
```

출처
- https://velog.io/@kshired/Spring-Redis%EC%97%90%EC%84%9C-%EA%B0%9D%EC%B2%B4-%EC%BA%90%EC%8B%B1%ED%95%98%EA%B8%B0
- https://blog.naver.com/PostView.nhn?blogId=cutesboy3&logNo=222285071695&categoryNo=22&parentCategoryNo=0&viewDate=&currentPage=1&postListTopCurrentPage=1&from=postView