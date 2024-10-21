package com.app.test.Exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> unauthorizedException(InvalidTokenException ex) {
    ErrorResponse response = new ErrorResponse();
    response.setErrorCode("UNAUTHORIZED");
    response.setErrorMessage(ex.getMessage());
    response.setTimestamp(new Date());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

}
