package com.chiril.library.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ReturnReceipt {
    private final Loan loan;
    private final BigDecimal penalty;
    private final LocalDate evaluatedOn;

    public ReturnReceipt(Loan loan, BigDecimal penalty, LocalDate evaluatedOn) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        if (penalty == null) {
            throw new IllegalArgumentException("penalty null");
        }
        if (evaluatedOn == null) {
            throw new IllegalArgumentException("evaluatedOn null");
        }
        this.loan = loan;
        this.penalty = penalty;
        this.evaluatedOn = evaluatedOn;
    }

    public Loan getLoan() {
        return loan;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public LocalDate getEvaluatedOn() {
        return evaluatedOn;
    }

    @Override
    public String toString() {
        return "ReturnReceipt{loanId='" + loan.getLoanId() + "', itemId='" + loan.getItemId() +
                "', returnedOn=" + loan.getReturnDate() + ", evaluatedOn=" + evaluatedOn +
                ", penalty=" + penalty + "}";
    }
}
