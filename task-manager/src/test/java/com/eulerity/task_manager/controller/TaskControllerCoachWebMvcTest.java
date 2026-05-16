package com.eulerity.task_manager.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.eulerity.task_manager.dto.CoachFeedbackResponse;
import com.eulerity.task_manager.dto.TaskResponse;
import com.eulerity.task_manager.exception.GlobalExceptionHandler;
import com.eulerity.task_manager.model.Priority;
import com.eulerity.task_manager.model.Status;
import com.eulerity.task_manager.service.OpenAiProductivityCoachService;
import com.eulerity.task_manager.service.TaskService;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerCoachWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TaskService taskService;

	@MockBean
	private OpenAiProductivityCoachService productivityCoachService;

	@Test
	void postFeedback_returnsCoachParagraph() throws Exception {
		when(taskService.findAll()).thenReturn(List.of(TaskResponse.builder()
				.id(1L)
				.title("Ship demo")
				.dueDate(java.time.LocalDate.of(2026, 6, 1))
				.priority(Priority.HIGH)
				.status(Status.TODO)
				.build()));
		when(productivityCoachService.feedback(anyList())).thenReturn(new CoachFeedbackResponse(
				"Start with Ship demo since it is high priority. You have got this."));

		mockMvc.perform(post("/tasks/feedback").contentType(APPLICATION_JSON).content("{}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.feedback").value(
						"Start with Ship demo since it is high priority. You have got this."));
	}
}
