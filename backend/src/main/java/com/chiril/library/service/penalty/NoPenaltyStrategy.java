package com.chiril.library.service.penalty;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class NoPenaltyStrategy implements PenaltyStrategy {
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
        return BigDecimal.ZERO;
    }
}
