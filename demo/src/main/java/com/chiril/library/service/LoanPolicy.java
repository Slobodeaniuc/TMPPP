package com.chiril.library.service;

import java.time.LocalDate;

import com.chiril.library.domain.LibraryItem;

public interface LoanPolicy {
    LocalDate computeDueDate(LibraryItem item, LocalDate loanDate);
}