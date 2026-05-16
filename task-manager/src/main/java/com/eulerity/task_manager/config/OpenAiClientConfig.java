package com.eulerity.task_manager.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiClientConfig {

	@Bean
	RestClient openAiRestClient(RestClient.Builder builder, OpenAiProperties props) {
		var rb = builder.baseUrl(props.url()).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		String key = props.key();
		if (key != null && !key.isBlank()) {
			rb = rb.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + key.trim());
		}
		return rb.build();
	}
}
