package com.eulerity.task_manager;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Bootstrap: component-scan starts in this package ({@code com.eulerity.task_manager}). */
@SpringBootApplication
public class TaskManagerApplication {

	public static void main(String[] args) {
		// Load task-manager/.env into JVM system properties so OPEN_AI_KEY / OPENAI_API_KEY bind before Spring reads properties.
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(e -> {
			String k = e.getKey();
			if (System.getenv(k) == null && System.getProperty(k) == null) {
				System.setProperty(k, e.getValue());
			}
		});
		SpringApplication.run(TaskManagerApplication.class, args); // embedded Tomcat + auto-config
	}

}
