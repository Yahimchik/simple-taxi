package com.simple.taxi.user.exception;

import com.simple.taxi.user.model.enums.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleApiException(
            ApiException ex,
            ServerWebExchange exchange,
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
                .path(exchange.getRequest().getURI().getPath())
                .status(resolveHttpStatus(ex.getErrorType()).value())
                .build();

        return Mono.just(ResponseEntity
                .status(resolveHttpStatus(ex.getErrorType()))
                .body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleUnexpected(
            Exception ex,
            ServerWebExchange exchange
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ex.getMessage())
                .timestamp(ZonedDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return Mono.just(ResponseEntity.internalServerError().body(response));
    }

    private HttpStatus resolveHttpStatus(ErrorType errorType) {
        return switch (errorType) {
            case USER_PROFILE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case FIELD_VALIDATION -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}