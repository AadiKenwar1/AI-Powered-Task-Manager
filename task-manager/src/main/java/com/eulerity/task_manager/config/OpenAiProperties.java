package com.eulerity.task_manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai.api")
public record OpenAiProperties(String key, String url, String model) {
}
