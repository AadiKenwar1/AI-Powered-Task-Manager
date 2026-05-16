package com.eulerity.task_manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Starts the full Spring application as in production; fails if any bean (DataSource, JPA,
 * controllers, services) cannot be created or wired.
 */
@SpringBootTest
class TaskManagerApplicationTests {

	@Test
	void contextLoads() {
		// No assertions needed: failure happens during context bootstrap (before this line runs).
	}

}
