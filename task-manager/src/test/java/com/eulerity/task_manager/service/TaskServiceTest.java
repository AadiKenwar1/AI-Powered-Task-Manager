package com.eulerity.task_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eulerity.task_manager.dto.TaskRequest;
import com.eulerity.task_manager.dto.TaskResponse;
import com.eulerity.task_manager.model.Priority;
import com.eulerity.task_manager.model.Status;
import com.eulerity.task_manager.model.Task;
import com.eulerity.task_manager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskService taskService;

	private static TaskRequest sampleRequest() {
		return TaskRequest.builder()
				.title("Quarterly report")
				.description("Finance team")
				.dueDate(LocalDate.of(2026, 6, 1))
				.priority(Priority.HIGH)
				.status(Status.TODO)
				.build();
	}

	@Test
	void create_persistsAndMapsToResponse() {
		TaskRequest request = sampleRequest();
		Task persisted = Task.builder()
				.id(10L)
				.title(request.getTitle())
				.description(request.getDescription())
				.dueDate(request.getDueDate())
				.priority(request.getPriority())
				.status(request.getStatus())
				.build();
		when(taskRepository.save(any(Task.class))).thenReturn(persisted);

		TaskResponse response = taskService.create(request);

		assertThat(response.getId()).isEqualTo(10L);
		assertThat(response.getTitle()).isEqualTo("Quarterly report");
		assertThat(response.getPriority()).isEqualTo(Priority.HIGH);
		verify(taskRepository).save(any(Task.class));
	}

	@Test
	void findAll_returnsMappedTasks() {
		Task task = Task.builder()
				.id(2L)
				.title("A")
				.description(null)
				.dueDate(LocalDate.of(2026, 1, 15))
				.priority(Priority.LOW)
				.status(Status.IN_PROGRESS)
				.build();
		when(taskRepository.findAll()).thenReturn(List.of(task));

		List<TaskResponse> responses = taskService.findAll();

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).getId()).isEqualTo(2L);
		assertThat(responses.get(0).getStatus()).isEqualTo(Status.IN_PROGRESS);
	}

	@Test
	void findById_returnsTaskWhenPresent() {
		Task task = Task.builder()
				.id(7L)
				.title("X")
				.description("d")
				.dueDate(LocalDate.of(2026, 3, 3))
				.priority(Priority.MEDIUM)
				.status(Status.DONE)
				.build();
		when(taskRepository.findById(7L)).thenReturn(Optional.of(task));

		TaskResponse response = taskService.findById(7L);

		assertThat(response.getTitle()).isEqualTo("X");
		assertThat(response.getStatus()).isEqualTo(Status.DONE);
	}

	@Test
	void update_appliesRequestAndReturnsResponse() {
		Task existing = Task.builder()
				.id(5L)
				.title("Old")
				.description("old desc")
				.dueDate(LocalDate.of(2026, 1, 1))
				.priority(Priority.LOW)
				.status(Status.TODO)
				.build();
		when(taskRepository.findById(5L)).thenReturn(Optional.of(existing));
		when(taskRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

		TaskRequest update = TaskRequest.builder()
				.title("New title")
				.description(null)
				.dueDate(LocalDate.of(2026, 12, 31))
				.priority(Priority.HIGH)
				.status(Status.IN_PROGRESS)
				.build();

		TaskResponse response = taskService.update(5L, update);

		assertThat(response.getTitle()).isEqualTo("New title");
		assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 31));
		assertThat(existing.getPriority()).isEqualTo(Priority.HIGH);
		verify(taskRepository).save(existing);
	}

	@Test
	void deleteById_removesWhenPresent() {
		when(taskRepository.existsById(3L)).thenReturn(true);

		taskService.deleteById(3L);

		verify(taskRepository).deleteById(3L);
	}
}
