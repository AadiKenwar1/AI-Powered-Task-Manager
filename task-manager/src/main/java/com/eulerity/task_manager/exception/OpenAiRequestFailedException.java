package com.eulerity.task_manager.exception;

public class OpenAiRequestFailedException extends RuntimeException {

	public OpenAiRequestFailedException(String message) {
		super(message);
	}

	public OpenAiRequestFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
