package com.bulentyilmaz.todoapp;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@RunWith(SpringRunner.class)
@SpringBootTest
class TodoAppApplicationTests {

	public static PostgreSQLContainer container = (PostgreSQLContainer) (new PostgreSQLContainer("postgres:14")
			.withDatabaseName("db")
			.withUsername("root")
			.withPassword("rootTest"))
			.withReuse(true);

	@Test
	void contextLoads() {
	}

}
