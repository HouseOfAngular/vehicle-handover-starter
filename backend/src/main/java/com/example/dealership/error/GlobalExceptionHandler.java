package com.example.dealership.error;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getAllErrors().stream()
                .map(GlobalExceptionHandler::toValidationError)
                .sorted(Comparator.comparing(ValidationError::field, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(ValidationError::message))
                .toList();
        return validationProblem(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex) {
        return validationProblem(List.of(toValidationError(ex.getCause())));
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Request conflicts with existing data");
    }

    private static ProblemDetail validationProblem(List<ValidationError> errors) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setProperty("errors", errors);
        return problem;
    }

    private static ValidationError toValidationError(ObjectError error) {
        String field = error instanceof FieldError fieldError ? fieldError.getField() : null;
        return new ValidationError(field, error.getDefaultMessage());
    }

    private static ValidationError toValidationError(Throwable cause) {
        if (!(cause instanceof JsonMappingException mapping) || mapping.getPath().isEmpty()) {
            return new ValidationError(null, "request body must be valid JSON");
        }
        String field = mapping.getPath().stream()
                .map(reference -> reference.getFieldName() != null
                        ? "." + reference.getFieldName()
                        : "[" + reference.getIndex() + "]")
                .collect(Collectors.joining())
                .replaceFirst("^\\.", "");
        if (cause instanceof InvalidFormatException invalidFormat && invalidFormat.getTargetType().isEnum()) {
            String allowed = Arrays.stream(invalidFormat.getTargetType().getEnumConstants())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            return new ValidationError(field, "must be one of: " + allowed);
        }
        return new ValidationError(field, "has an invalid value");
    }
}
