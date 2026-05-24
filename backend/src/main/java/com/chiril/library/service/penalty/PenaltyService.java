package com.chiril.library.service.penalty;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;
import com.chiril.library.service.exceptions.ItemNotFoundException;
import com.chiril.library.service.exceptions.LoanNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class PenaltyService {
    private final Catalog catalog;
    private final LoanRepository loanRepository;
    private final PenaltyStrategy penaltyStrategy;

    public PenaltyService(Catalog catalog, LoanRepository loanRepository, PenaltyStrategy penaltyStrategy) {
        if (catalog == null) {
            throw new IllegalArgumentException("catalog null");
        }
        if (loanRepository == null) {
            throw new IllegalArgumentException("loanRepository null");
        }
        if (penaltyStrategy == null) {
            throw new IllegalArgumentException("penaltyStrategy null");
        }
        this.catalog = catalog;
        this.loanRepository = loanRepository;
        this.penaltyStrategy = penaltyStrategy;
    }

    public BigDecimal calculatePenaltyForLoan(String loanId, LocalDate evaluationDate) {
        if (loanId == null || loanId.isBlank()) {
            throw new IllegalArgumentException("loanId invalid");
        }
        if (evaluationDate == null) {
            throw new IllegalArgumentException("evaluationDate null");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan inexistent: " + loanId));
        LibraryItem item = catalog.findById(loan.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item inexistent: " + loan.getItemId()));
        return penaltyStrategy.computePenalty(item, loan, evaluationDate);
    }

    public BigDecimal calculatePenaltyForActiveLoan(String itemId, LocalDate evaluationDate) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId invalid");
        }
        if (evaluationDate == null) {
            throw new IllegalArgumentException("evaluationDate null");
        }

        Loan loan = loanRepository.findActiveLoanByItemId(itemId)
                .orElseThrow(() -> new LoanNotFoundException("Nu exista imprumut activ pentru item: " + itemId));
        LibraryItem item = catalog.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item inexistent: " + itemId));
        return penaltyStrategy.computePenalty(item, loan, evaluationDate);
    }
}
