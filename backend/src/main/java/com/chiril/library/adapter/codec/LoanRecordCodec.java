package com.chiril.library.adapter.codec;

import com.chiril.library.domain.Loan;

import java.time.LocalDate;

public final class LoanRecordCodec {
    public String encode(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }

        String returnDate = loan.getReturnDate() == null ? "" : loan.getReturnDate().toString();
        return String.join("|",
                Base64Codec.encode(loan.getLoanId()),
                Base64Codec.encode(loan.getMemberId()),
                Base64Codec.encode(loan.getItemId()),
                loan.getLoanDate().toString(),
                loan.getDueDate().toString(),
                returnDate);
    }

    public Loan decode(String record) {
        if (record == null || record.isBlank()) {
            throw new IllegalArgumentException("record invalid");
        }

        String[] parts = record.split("\\|", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid loan record: expected 6 fields but got " + parts.length);
        }

        Loan loan = new Loan(
                Base64Codec.decode(parts[0]),
                Base64Codec.decode(parts[1]),
                Base64Codec.decode(parts[2]),
                LocalDate.parse(parts[3]),
                LocalDate.parse(parts[4]));

        if (!parts[5].isBlank()) {
            loan.markReturned(LocalDate.parse(parts[5]));
        }

        return loan;
    }
}
