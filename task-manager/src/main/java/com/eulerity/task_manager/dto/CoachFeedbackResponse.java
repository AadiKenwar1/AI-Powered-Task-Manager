package com.eulerity.task_manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

// Plain-language coach output for POST /tasks/feedback (structured JSON for the UI).
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CoachFeedbackResponse(String feedback) {
}
