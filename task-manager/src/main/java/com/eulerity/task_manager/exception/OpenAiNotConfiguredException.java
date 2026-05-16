package com.eulerity.task_manager.exception;

public class OpenAiNotConfiguredException extends RuntimeException {

	public OpenAiNotConfiguredException(String message) {
		super(message);
	}
}
