package com.chiril.library.service;

import java.time.LocalDate;

import com.chiril.library.domain.LibraryItem;

public final class DefaultLoanPolicy implements LoanPolicy {

    @Override
    public LocalDate computeDueDate(LibraryItem item, LocalDate loanDate) {

        return loanDate.plusDays(14);
    }
}