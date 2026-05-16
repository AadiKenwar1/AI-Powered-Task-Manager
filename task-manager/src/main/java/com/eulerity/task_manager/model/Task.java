package com.eulerity.task_manager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// JPA entity mapped to H2 table "tasks"; lifecycle managed by Hibernate.
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title; // required by assignment spec

	@Column(length = 4000)
	private String description; // optional longer text

	@Column(nullable = false)
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING) // persist LOW not ordinal 0
	@Column(nullable = false, length = 32)
	private Priority priority;

	@Enumerated(EnumType.STRING) // store TODO / IN_PROGRESS / DONE as strings
	@Column(nullable = false, length = 32)
	private Status status;
}
