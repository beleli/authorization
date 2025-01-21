package br.com.blitech.authorization;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class AuthorizationApplicationTests {

	@Test
	void contextLoads() {
		// This test is used to check if the application context can be loaded
	}
}
