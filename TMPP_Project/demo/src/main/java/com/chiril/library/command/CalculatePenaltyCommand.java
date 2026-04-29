package com.chiril.library.command;

import com.chiril.library.facade.LibraryFacade;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class CalculatePenaltyCommand implements LibraryCommand<BigDecimal> {
    private final LibraryFacade facade;
    private final String loanId;
    private final LocalDate evaluationDate;

    public CalculatePenaltyCommand(LibraryFacade facade, String loanId, LocalDate evaluationDate) {
        if (facade == null) {
            throw new IllegalArgumentException("facade null");
        }
        this.facade = facade;
        this.loanId = loanId;
        this.evaluationDate = evaluationDate;
    }

    @Override
    public BigDecimal execute() {
        return facade.calculatePenaltyForLoan(loanId, evaluationDate);
    }
}
