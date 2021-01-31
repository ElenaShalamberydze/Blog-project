package com.leverx.blog.config;

import com.leverx.blog.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String validationFailedExceptionHandler(ValidationException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-handle";
    }

    @ExceptionHandler(ValidatorInnerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String validatorInnerExceptionHandler(ValidatorInnerException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-handle";
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String forbiddenAccessExceptionHandler(ForbiddenAccessException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-handle";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFoundExceptionHandler(NotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-handle";
    }

    @ExceptionHandler(NotModifiedException.class)
    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    String NotModifiedExceptionHandler(NotModifiedException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-handle";
    }

}
