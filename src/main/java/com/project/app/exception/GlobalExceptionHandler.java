package com.project.app.exception;

import java.util.HashMap;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {

        Map<String, String> response = new HashMap<>();

        switch (ex.getMessage()) {

            case "Account does not exist":
                response.put("error", "User Not Found");
                response.put("message", "Account does not exist");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

            case "Incorrect OTP":
                response.put("error", "Invalid OTP");
                response.put("message", "Incorrect OTP");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

            case "OTP expired":
                response.put("error", "OTP Expired");
                response.put("message", "OTP expired");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

            case "Use strong password":
                response.put("error", "Password Too Weak");
                response.put("message", "Use strong password");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

            case "Too many attempts":
            case "Please wait 30 seconds before requesting another OTP":
                response.put("error", "Too Many Requests");
                response.put("message", "Try again later");
                return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);

            default:
                response.put("error", "Bad Request");
                response.put("message", ex.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
   
}