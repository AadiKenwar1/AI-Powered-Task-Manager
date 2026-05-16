package com.eulerity.task_manager.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Central mapping from thrown exceptions to HTTP status codes for all @RestController methods.
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Void> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.notFound().build(); // 404
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
		// @Valid failures on @RequestBody (e.g. missing title)
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	@ExceptionHandler(OpenAiNotConfiguredException.class)
	public ResponseEntity<Map<String, String>> handleOpenAiNotConfigured(OpenAiNotConfiguredException ex) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(OpenAiRequestFailedException.class)
	public ResponseEntity<Map<String, String>> handleOpenAiFailed(OpenAiRequestFailedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("message", ex.getMessage()));
	}
}
