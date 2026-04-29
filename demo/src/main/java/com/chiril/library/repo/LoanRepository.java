package com.chiril.library.repo;

import java.util.List;
import java.util.Optional;

import com.chiril.library.domain.Loan;

public interface LoanRepository {
    void save(Loan loan);
    Optional<Loan> findById(String loanId);
    Optional<Loan> findActiveLoanByItemId(String itemId);
    List<Loan> findByMemberId(String memberId);
}