package com.eulerity.task_manager.dto;

import com.eulerity.task_manager.model.Priority;
import com.eulerity.task_manager.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Outgoing JSON shape for clients; mirrors Task fields including generated id.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

	private Long id;
	private String title;
	private String description;
	private LocalDate dueDate;
	private Priority priority;
	private Status status;
}
