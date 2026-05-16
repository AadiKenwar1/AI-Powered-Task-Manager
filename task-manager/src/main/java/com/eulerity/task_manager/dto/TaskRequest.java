package com.eulerity.task_manager.dto;

import com.eulerity.task_manager.model.Priority;
import com.eulerity.task_manager.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Incoming JSON for POST /tasks and PUT /tasks/{id}; validated before service layer runs.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

	@NotBlank(message = "title is required")
	private String title;

	private String description; // optional — no @NotNull

	@NotNull(message = "dueDate is required")
	private LocalDate dueDate;

	@NotNull(message = "priority is required")
	private Priority priority;

	@NotNull(message = "status is required")
	private Status status;
}
