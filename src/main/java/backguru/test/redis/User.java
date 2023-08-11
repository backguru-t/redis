package backguru.test.redis;

import java.io.Serializable;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
	private String name;
	private int age;
}
