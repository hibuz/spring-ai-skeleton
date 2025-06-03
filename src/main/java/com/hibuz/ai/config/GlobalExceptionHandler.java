package com.hibuz.ai.config;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	//@ExceptionHandler(Exception.class)
	public ProblemDetail handleServerException(Exception ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(
				HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		pd.setTitle("Internal Server Error");
		pd.setProperty("timestamp", LocalDateTime.now());
		return pd;
	}
}
