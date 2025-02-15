package org.springboot.event_horizon.exceptions;

import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    /*
    The controller handles known exceptions (like ApiException) and returns custom responses with ResponseEntity.
    The global exception handler is a fallback for any uncaught exceptions to ensure that the application returns a standardized response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getBindingResult().getFieldError().getDefaultMessage());
        return ResponseEntity.status(400).body(new ApiResponse(ex.getStatusCode().value(),errors,"Errors Occured check the data"));
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(400)
                .body(new ApiResponse(400, null, "Invalid email or password"));
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
        System.out.println(" Global ApiException: " + ex.getMessage());
        ApiResponse response = new ApiResponse(ex.getStatusCode(), null, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }




    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        System.out.println("Got Exception Exception e"+ ex.getMessage());
        ApiResponse response = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}