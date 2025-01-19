package com.javacademy.new_york_times.controller;

import com.javacademy.new_york_times.exception.PageNumberLessZeroException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PageNumberLessZeroException.class)
    public ResponseEntity<?> handePageNumberException(PageNumberLessZeroException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
}
