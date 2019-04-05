package com.sora.crawler.crawler.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(Exception.class)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", ex.getMessage());
        errorInfo.put("status", HttpStatus.BAD_REQUEST);
        errorInfo.put("status_code", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }
}
