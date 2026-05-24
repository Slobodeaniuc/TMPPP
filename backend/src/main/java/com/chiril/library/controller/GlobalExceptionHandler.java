package com.chiril.library.controller;

import com.chiril.library.dto.ErrorDto;
import com.chiril.library.service.exceptions.ItemAlreadyLoanedException;
import com.chiril.library.service.exceptions.ItemNotFoundException;
import com.chiril.library.service.exceptions.LoanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleItemNotFound(ItemNotFoundException ex) {
        return new ErrorDto("ITEM_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleLoanNotFound(LoanNotFoundException ex) {
        return new ErrorDto("LOAN_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ItemAlreadyLoanedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleAlreadyLoaned(ItemAlreadyLoanedException ex) {
        return new ErrorDto("ITEM_ALREADY_LOANED", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequest(IllegalArgumentException ex) {
        return new ErrorDto("BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleConflict(IllegalStateException ex) {
        return new ErrorDto("CONFLICT", ex.getMessage());
    }
}
