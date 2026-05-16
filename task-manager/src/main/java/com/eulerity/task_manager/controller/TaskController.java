package com.eulerity.task_manager.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eulerity.task_manager.dto.CoachFeedbackResponse;
import com.eulerity.task_manager.dto.TaskRequest;
import com.eulerity.task_manager.dto.TaskResponse;
import com.eulerity.task_manager.service.OpenAiProductivityCoachService;
import com.eulerity.task_manager.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// HTTP adapter only: delegates to TaskService; @Valid triggers bean validation on the body.
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;
	private final OpenAiProductivityCoachService productivityCoachService;

	@PostMapping
	public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
		TaskResponse created = taskService.create(request);
		URI location = URI.create("/tasks/" + created.getId()); // RFC 7231 Location for 201 Created
		return ResponseEntity.created(location).body(created);
	}

	/** Sends the current task list to the model; returns one prioritization paragraph + motivation. */
	@PostMapping("/feedback")
	public CoachFeedbackResponse coachFeedback() {
		return productivityCoachService.feedback(taskService.findAll());
	}

	@GetMapping
	public List<TaskResponse> list() {
		return taskService.findAll();
	}

	@GetMapping("/{id}")
	public TaskResponse get(@PathVariable Long id) {
		return taskService.findById(id); // 404 from GlobalExceptionHandler if missing
	}

	@PutMapping("/{id}")
	public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
		return taskService.update(id, request); // full replace from request body
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		taskService.deleteById(id);
		return ResponseEntity.noContent().build(); // 204 — no JSON body
	}
}
