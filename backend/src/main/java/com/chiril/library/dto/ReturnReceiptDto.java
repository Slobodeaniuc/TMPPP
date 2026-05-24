package com.chiril.library.dto;

import com.chiril.library.domain.ReturnReceipt;

public record ReturnReceiptDto(
        LoanDto loan,
        String penalty,
        String evaluatedOn
) {
    public static ReturnReceiptDto from(ReturnReceipt receipt) {
        return new ReturnReceiptDto(
                LoanDto.from(receipt.getLoan()),
                receipt.getPenalty().toPlainString(),
                receipt.getEvaluatedOn().toString()
        );
    }
}
