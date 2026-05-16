package com.eulerity.task_manager.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.eulerity.task_manager.config.OpenAiProperties;
import com.eulerity.task_manager.dto.CoachFeedbackResponse;
import com.eulerity.task_manager.dto.TaskResponse;
import com.eulerity.task_manager.exception.OpenAiNotConfiguredException;
import com.eulerity.task_manager.exception.OpenAiRequestFailedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiProductivityCoachService {

	private static final String SYSTEM_PROMPT = """
			You are a warm, concise productivity coach. The user will send a JSON array of tasks.
			Each task has: id, title, description (may be null), dueDate (yyyy-MM-DD), priority (LOW|MEDIUM|HIGH), status (TODO|IN_PROGRESS|DONE).

			Write exactly one short paragraph of prose (about 4–8 sentences). Order your guidance from the most important or urgent work first down to the least;
			use priority, due dates, and status to decide. Briefly acknowledge completed tasks without dwelling on them.
			End with one motivating sentence. Do not use markdown, bullet lists, or numbered lists—plain paragraph only.
			""";

	private final RestClient openAiRestClient;
	private final OpenAiProperties props;
	private final ObjectMapper objectMapper;

	public CoachFeedbackResponse feedback(List<TaskResponse> tasks) {
		if (tasks.isEmpty()) {
			return new CoachFeedbackResponse(
					"You do not have any tasks saved yet. Add a few concrete items with realistic due dates—starting small builds momentum. "
							+ "When you are ready, click Feedback again and we will help you prioritize.");
		}

		if (props.key() == null || props.key().isBlank()) {
			throw new OpenAiNotConfiguredException(
					"Missing API key: set environment variable OPEN_AI_KEY or OPENAI_API_KEY, or add it to a local .env file.");
		}

		String tasksJson;
		try {
			tasksJson = objectMapper.writeValueAsString(tasks);
		}
		catch (Exception ex) {
			throw new OpenAiRequestFailedException("Could not serialize tasks: " + ex.getMessage(), ex);
		}

		Map<String, Object> body = Map.of(
				"model", props.model(),
				"messages", List.of(
						Map.of("role", "system", "content", SYSTEM_PROMPT),
						Map.of("role", "user", "content", "Here are my current tasks as JSON:\n" + tasksJson)),
				"temperature", 0.65);

		try {
			String raw = openAiRestClient.post()
					.uri("/chat/completions")
					.body(body)
					.retrieve()
					.body(String.class);
			String paragraph = extractAssistantText(raw);
			if (paragraph.isBlank()) {
				throw new OpenAiRequestFailedException("OpenAI returned an empty coach message.");
			}
			return new CoachFeedbackResponse(paragraph.trim());
		}
		catch (RestClientResponseException ex) {
			throw new OpenAiRequestFailedException(
					"OpenAI HTTP " + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString(), ex);
		}
		catch (Exception ex) {
			throw new OpenAiRequestFailedException("Failed to process OpenAI response: " + ex.getMessage(), ex);
		}
	}

	private String extractAssistantText(String rawJson) throws java.io.IOException {
		JsonNode root = objectMapper.readTree(rawJson);
		JsonNode content = root.path("choices").path(0).path("message").path("content");
		if (content.isMissingNode()) {
			return "";
		}
		return content.asText("");
	}
}
