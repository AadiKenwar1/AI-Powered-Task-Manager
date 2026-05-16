package com.eulerity.task_manager.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String VALID_TASK_JSON = """
			{"title":"Integration task","description":"from test","dueDate":"2026-07-20","priority":"MEDIUM","status":"TODO"}
			""";

	@Test
	void postTasks_createsReturns201AndLocation() throws Exception {
		mockMvc.perform(post("/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_TASK_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("/tasks/")))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.title").value("Integration task"))
				.andExpect(jsonPath("$.priority").value("MEDIUM"));
	}

	@Test
	void postTasks_missingTitle_returns400() throws Exception {
		String invalid = """
				{"title":"","dueDate":"2026-07-20","priority":"LOW","status":"DONE"}
				""";
		mockMvc.perform(post("/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalid))
				.andExpect(status().isBadRequest());
	}

	@Test
	void crudEndpoints_fullLifecycle() throws Exception {
		MvcResult created = mockMvc.perform(post("/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_TASK_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		JsonNode createdBody = objectMapper.readTree(created.getResponse().getContentAsString());
		long id = createdBody.get("id").asLong();

		mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id))
				.andExpect(jsonPath("$[0].title").value("Integration task"));

		mockMvc.perform(get("/tasks/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.status").value("TODO"));

		String updateJson = """
				{"title":"Integration task","description":"updated","dueDate":"2026-08-01","priority":"HIGH","status":"IN_PROGRESS"}
				""";
		mockMvc.perform(put("/tasks/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.priority").value("HIGH"));

		mockMvc.perform(delete("/tasks/" + id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/tasks/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	void getTask_unknownId_returns404() throws Exception {
		mockMvc.perform(get("/tasks/999999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTask_unknownId_returns404() throws Exception {
		mockMvc.perform(delete("/tasks/999998"))
				.andExpect(status().isNotFound());
	}
}
