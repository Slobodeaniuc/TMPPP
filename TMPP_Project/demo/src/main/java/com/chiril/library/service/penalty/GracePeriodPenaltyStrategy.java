package com.chiril.library.service.penalty;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class GracePeriodPenaltyStrategy implements PenaltyStrategy {
    private final PenaltyStrategy delegate;
    private final int graceDays;

    public GracePeriodPenaltyStrategy(PenaltyStrategy delegate, int graceDays) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate null");
        }
        if (graceDays < 0) {
            throw new IllegalArgumentException("graceDays invalid");
        }
        this.delegate = delegate;
        this.graceDays = graceDays;
    }

    @Override
    public BigDecimal computePenalty(LibraryItem item, Loan loan, LocalDate evaluationDate) {
        if (item == null) {
            throw new IllegalArgumentException("item null");
        }
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        if (evaluationDate == null) {
            throw new IllegalArgumentException("evaluationDate null");
        }
        if (!evaluationDate.isAfter(loan.getDueDate())) {
            return BigDecimal.ZERO;
        }

        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), evaluationDate);
        if (overdueDays <= graceDays) {
            return BigDecimal.ZERO;
        }

        return delegate.computePenalty(item, loan, evaluationDate.minusDays(graceDays));
    }
}
