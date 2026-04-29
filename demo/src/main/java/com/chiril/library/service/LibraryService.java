package com.chiril.library.service;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;
import com.chiril.library.service.exceptions.ItemAlreadyLoanedException;
import com.chiril.library.service.exceptions.ItemNotFoundException;
import com.chiril.library.service.exceptions.LoanNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class LibraryService {
    private final Catalog catalog;
    private final LoanRepository loanRepository;
    private final LoanPolicy loanPolicy;

    public LibraryService(Catalog catalog, LoanRepository loanRepository, LoanPolicy loanPolicy) {
        if (catalog == null) throw new IllegalArgumentException("catalog null");
        if (loanRepository == null) throw new IllegalArgumentException("loanRepository null");
        if (loanPolicy == null) throw new IllegalArgumentException("loanPolicy null");
        this.catalog = catalog;
        this.loanRepository = loanRepository;
        this.loanPolicy = loanPolicy;
    }

    public Loan borrowItem(String memberId, String itemId) {
        if (memberId == null || memberId.trim().isEmpty()) throw new IllegalArgumentException("memberId invalid");
        if (itemId == null || itemId.trim().isEmpty()) throw new IllegalArgumentException("itemId invalid");

        LibraryItem item = catalog.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item inexistent: " + itemId));

        loanRepository.findActiveLoanByItemId(itemId).ifPresent(l -> {
            throw new ItemAlreadyLoanedException("Item deja împrumutat: " + itemId);
        });

        LocalDate now = LocalDate.now();
        LocalDate due = loanPolicy.computeDueDate(item, now);

        Loan loan = new Loan(UUID.randomUUID().toString(), memberId, itemId, now, due);
        loanRepository.save(loan);
        return loan;
    }

    public Loan returnItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) throw new IllegalArgumentException("itemId invalid");

        Loan activeLoan = loanRepository.findActiveLoanByItemId(itemId)
                .orElseThrow(() -> new LoanNotFoundException("Nu există împrumut activ pentru item: " + itemId));

        activeLoan.markReturned(LocalDate.now());
        loanRepository.save(activeLoan); // in-memory: suprascrie aceeași referința
        return activeLoan;
    }

    public List<Loan> listLoansForMember(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) throw new IllegalArgumentException("memberId invalid");
        return loanRepository.findByMemberId(memberId);
    }
}