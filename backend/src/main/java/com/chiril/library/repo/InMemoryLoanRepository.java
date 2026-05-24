package com.chiril.library.repo;

import java.util.*;

import com.chiril.library.domain.Loan;

public final class InMemoryLoanRepository implements LoanRepository {
    private static InMemoryLoanRepository instance;
    private final Map<String, Loan> loans = new HashMap<>();

    private InMemoryLoanRepository() {
    }

    public static synchronized InMemoryLoanRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryLoanRepository();
        }
        return instance;
    }

    @Override
    public void save(Loan loan) {
        if (loan == null)
            throw new IllegalArgumentException("loan null");
        loans.put(loan.getLoanId(), loan);
    }

    @Override
    public Optional<Loan> findById(String loanId) {
        if (loanId == null || loanId.isBlank())
            return Optional.empty();
        return Optional.ofNullable(loans.get(loanId));
    }

    @Override
    public Optional<Loan> findActiveLoanByItemId(String itemId) {
        if (itemId == null || itemId.isBlank())
            return Optional.empty();
        return loans.values().stream()
                .filter(l -> l.getItemId().equals(itemId) && l.isActive())
                .findFirst();
    }

    @Override
    public List<Loan> findByMemberId(String memberId) {
        if (memberId == null || memberId.isBlank())
            return List.of();
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans.values()) {
            if (memberId.equals(l.getMemberId()))
                result.add(l);
        }
        return result;
    }
}