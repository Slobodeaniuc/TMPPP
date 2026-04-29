package com.chiril.library.domain;

import java.time.LocalDate;

public final class Loan {
    private final String loanId;
    private final String memberId;
    private final String itemId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate; // null = încă activ

    public Loan(String loanId, String memberId, String itemId, LocalDate loanDate, LocalDate dueDate) {
        if (loanId == null || loanId.isBlank()) throw new IllegalArgumentException("loanId invalid");
        if (memberId == null || memberId.isBlank()) throw new IllegalArgumentException("memberId invalid");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("itemId invalid");
        if (loanDate == null) throw new IllegalArgumentException("loanDate invalid");
        if (dueDate == null) throw new IllegalArgumentException("dueDate invalid");
        this.loanId = loanId;
        this.memberId = memberId;
        this.itemId = itemId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public String getLoanId() { return loanId; }
    public String getMemberId() { return memberId; }
    public String getItemId() { return itemId; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public boolean isActive() {
        return returnDate == null;
    }

    public void markReturned(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("return date invalid");
        if (!isActive()) throw new IllegalStateException("Loan deja închis");
        this.returnDate = date;
    }

    @Override
    public String toString() {
        return "Loan{loanId='" + loanId + "', memberId='" + memberId + "', itemId='" + itemId +
                "', loanDate=" + loanDate + ", dueDate=" + dueDate + ", returnDate=" + returnDate + "}";
    }
}