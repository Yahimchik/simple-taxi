package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.dto.ApiErrorResponse;
import com.simple.taxi.auth.model.enums.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        String localizedMessage = messageSource.getMessage(
                ex.getErrorType().name(),
                ex.getParams(),
                ex.getMessage(),
                locale
        );

        ApiErrorResponse response = ApiErrorResponse.builder()
                .errorType(ex.getErrorType())
                .message(localizedMessage)
                .timestamp(ex.getTime())
                .details(ex.getParams())
                .path(request.getRequestURI())
                .status(resolveHttpStatus(ex.getErrorType()).value())
                .build();

        return ResponseEntity
                .status(resolveHttpStatus(ex.getErrorType()))
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorResponse response = ApiErrorResponse.builder()
                .errorType(ErrorType.FIELD_VALIDATION)
                .message(message)
                .timestamp(ZonedDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ex.getMessage())
                .timestamp(ZonedDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

    private HttpStatus resolveHttpStatus(ErrorType errorType) {
        return switch (errorType) {
            case USER_NOT_FOUND, UNSUPPORTED_LOGIN_TYPE, VERIFICATION_TOKEN_NOT_FOUND, NO_SUCH_ROLE_AT_USER ->
                    HttpStatus.NOT_FOUND;
            case INVALID_CREDENTIALS, REFRESH_TOKEN_INVALID, REFRESH_TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED, VERIFICATION_TOKEN_EXPIRED, VERIFICATION_TOKEN_TOO_FREQUENT, USER_NOT_ACTIVATED,
                 USER_BLOCKED, TWO_FACTOR_REQUIRED, CAPTCHA_REQUIRED -> HttpStatus.FORBIDDEN;
            case FIELD_VALIDATION -> HttpStatus.BAD_REQUEST;
            case USER_EMAIL_EXISTS, USER_PHONE_EXISTS, NEW_PASSWORD_EQUALS_TO_OLD, USER_ALREADY_ACTIVE ->
                    HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}