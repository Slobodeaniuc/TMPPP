package com.chiril.library.service.decorator;

import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.service.LoanPolicy;

import java.time.LocalDate;

public final class ItemTypeLoanPolicyDecorator extends LoanPolicyDecorator {
    private static final int MAX_DVD_LOAN_DAYS = 7;

    public ItemTypeLoanPolicyDecorator(LoanPolicy delegate) {
        super(delegate);
    }

    @Override
    public LocalDate computeDueDate(LibraryItem item, LocalDate loanDate) {
        LocalDate dueDate = delegate.computeDueDate(item, loanDate);

        if (item instanceof Dvd) {
            LocalDate dvdCapDate = loanDate.plusDays(MAX_DVD_LOAN_DAYS);
            if (dueDate.isAfter(dvdCapDate)) {
                return dvdCapDate;
            }
        }

        return dueDate;
    }
}
