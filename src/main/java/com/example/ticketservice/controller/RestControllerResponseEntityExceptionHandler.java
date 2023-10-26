package com.example.ticketservice.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class RestControllerResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { IllegalArgumentException.class, ConstraintViolationException.class })
	protected ResponseEntity<Object> handleIllegalArgument(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, "Illegal argument(s).", new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { NoSuchElementException.class })
	protected ResponseEntity<Object> handleNoSuchElement(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, "Entity not found.", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { DataIntegrityViolationException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, "Data integrity violation.", new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

}
