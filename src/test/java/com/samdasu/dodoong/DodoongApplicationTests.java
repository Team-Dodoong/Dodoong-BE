package com.samdasu.dodoong;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@TestPropertySource(properties = {
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=true",
		"jwt.secret=Y29kZXgtdGVzdC1qd3Qtc2VjcmV0LWtleS0zMi1ieXRlcyEh",
		"jwt.access-token-expiration=1800000",
		"jwt.refresh-token-expiration=1209600000"
})
class DodoongApplicationTests {
	@Container
	@ServiceConnection
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

	@Test
	void contextLoads() {
	}

}
