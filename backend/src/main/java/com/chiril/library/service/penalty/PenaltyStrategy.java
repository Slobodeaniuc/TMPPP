package com.chiril.library.service.penalty;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PenaltyStrategy {
    BigDecimal computePenalty(LibraryItem item, Loan loan, LocalDate evaluationDate);
}
