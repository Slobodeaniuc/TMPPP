package com.chiril.library.notification;

import com.chiril.library.domain.Loan;

public abstract class LoanNotification {
    protected final NotificationChannel channel;

    protected LoanNotification(NotificationChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel null");
        }
        this.channel = channel;
    }

    public final void sendForLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        channel.send(loan.getMemberId(), buildMessage(loan));
    }

    protected abstract String buildMessage(Loan loan);
}
