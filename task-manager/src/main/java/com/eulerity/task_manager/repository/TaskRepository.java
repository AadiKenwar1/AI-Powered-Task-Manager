package com.eulerity.task_manager.repository;

import com.eulerity.task_manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

// Standard CRUD + paging hooks from Spring Data; Task PK type is Long.
public interface TaskRepository extends JpaRepository<Task, Long> {
}
