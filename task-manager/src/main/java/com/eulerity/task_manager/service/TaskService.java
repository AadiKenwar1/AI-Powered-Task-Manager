package com.eulerity.task_manager.service;

import com.eulerity.task_manager.dto.TaskRequest;
import com.eulerity.task_manager.dto.TaskResponse;
import com.eulerity.task_manager.exception.ResourceNotFoundException;
import com.eulerity.task_manager.model.Task;
import com.eulerity.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Encapsulates task rules and persistence; maps entities to API DTOs.
@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;

	@Transactional
	public TaskResponse create(TaskRequest request) {
		Task task = Task.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.dueDate(request.getDueDate())
				.priority(request.getPriority())
				.status(request.getStatus())
				.build();
		return toResponse(taskRepository.save(task)); // id assigned after flush
	}

	@Transactional(readOnly = true)
	public List<TaskResponse> findAll() {
		return taskRepository.findAll().stream().map(this::toResponse).toList(); // stable order not guaranteed unless sorted
	}

	@Transactional(readOnly = true)
	public TaskResponse findById(Long id) {
		return taskRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
	}

	@Transactional
	public TaskResponse update(Long id, TaskRequest request) {
		Task task = taskRepository.findById(id) // load-managed entity so setter updates persist
				.orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setDueDate(request.getDueDate());
		task.setPriority(request.getPriority());
		task.setStatus(request.getStatus());
		return toResponse(taskRepository.save(task));
	}

	@Transactional
	public void deleteById(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task not found: " + id); // align DELETE with GET 404 semantics
		}
		taskRepository.deleteById(id);
	}

	// Keeps JPA annotations off JSON returned to clients.
	private TaskResponse toResponse(Task task) {
		return TaskResponse.builder()
				.id(task.getId())
				.title(task.getTitle())
				.description(task.getDescription())
				.dueDate(task.getDueDate())
				.priority(task.getPriority())
				.status(task.getStatus())
				.build();
	}
}
