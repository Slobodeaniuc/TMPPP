package com.chiril.library.notification;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;
import com.chiril.library.repo.Catalog;
import com.chiril.library.service.penalty.PenaltyStrategy;

import java.math.BigDecimal;

public final class ReturnLoanNotification extends LoanNotification {
    private final Catalog catalog;
    private final PenaltyStrategy penaltyStrategy;

    public ReturnLoanNotification(NotificationChannel channel) {
        super(channel);
        this.catalog = null;
        this.penaltyStrategy = null;
    }

    public ReturnLoanNotification(NotificationChannel channel, Catalog catalog, PenaltyStrategy penaltyStrategy) {
        super(channel);
        if (catalog == null) {
            throw new IllegalArgumentException("catalog null");
        }
        if (penaltyStrategy == null) {
            throw new IllegalArgumentException("penaltyStrategy null");
        }
        this.catalog = catalog;
        this.penaltyStrategy = penaltyStrategy;
    }

    @Override
    protected String buildMessage(Loan loan) {
        String baseMessage = "Returned item " + loan.getItemId() + " on " + loan.getReturnDate();
        if (catalog == null || penaltyStrategy == null || loan.getReturnDate() == null) {
            return baseMessage;
        }

        LibraryItem item = catalog.findById(loan.getItemId()).orElse(null);
        if (item == null) {
            return baseMessage;
        }

        BigDecimal penalty = penaltyStrategy.computePenalty(item, loan, loan.getReturnDate());
        return baseMessage + " (penalty " + penalty + ")";
    }
}
