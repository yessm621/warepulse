package com.me.shipmentservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ApiResponse> exception(ShipmentServiceException e) {
        log.error("[exceptionHandler] ex", e);

        ErrorMessage errorMessage = ErrorMessage.create(e.getErrorCode().getCode(), e.getErrorCode().getMessage());

        return new ResponseEntity<>(ApiResponse.fail(errorMessage), e.getErrorCode().getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> exception(RuntimeException e) {
        log.error("[exceptionHandler] ex", e);

        ErrorMessage errorMessage = ErrorMessage.create(ErrorCode.RUNTIME_EXCEPTION.getCode(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.fail(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> exception(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);

        ErrorMessage errorMessage = ErrorMessage.create(ErrorCode.PARAMETER_INCORRECT.getCode(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.fail(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> exception(AccessDeniedException e) {
        log.error("[exceptionHandler] ex", e);

        ErrorMessage errorMessage = ErrorMessage.create(ErrorCode.ACCESS_DENIED_EXCEPTION.getCode(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.fail(errorMessage), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> exception(Exception e) {
        log.error("[exceptionHandler] ex", e);

        ErrorMessage errorMessage = ErrorMessage.create(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.fail(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
