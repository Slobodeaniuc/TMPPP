package com.chiril.library.dto;

import com.chiril.library.domain.Loan;

public record LoanDto(
        String loanId,
        String memberId,
        String itemId,
        String loanDate,
        String dueDate,
        String returnDate,
        boolean active
) {
    public static LoanDto from(Loan loan) {
        return new LoanDto(
                loan.getLoanId(),
                loan.getMemberId(),
                loan.getItemId(),
                loan.getLoanDate().toString(),
                loan.getDueDate().toString(),
                loan.getReturnDate() != null ? loan.getReturnDate().toString() : null,
                loan.isActive()
        );
    }
}
