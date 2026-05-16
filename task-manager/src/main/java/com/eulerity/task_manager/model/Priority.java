package com.eulerity.task_manager.model;

// Task.priority allowed values; stored as VARCHAR via @Enumerated(STRING) on Task.
public enum Priority {
	LOW,
	MEDIUM,
	HIGH
}
