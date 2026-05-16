package com.eulerity.task_manager.exception;

// Signals a missing domain row; handled as HTTP 404 by GlobalExceptionHandler.
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
