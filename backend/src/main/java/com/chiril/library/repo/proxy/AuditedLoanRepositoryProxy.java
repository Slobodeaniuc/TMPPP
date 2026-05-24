package com.chiril.library.repo.proxy;

import com.chiril.library.domain.Loan;
import com.chiril.library.repo.LoanRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class AuditedLoanRepositoryProxy implements LoanRepository {
    private final LoanRepository delegate;
    private final Path auditFile;

    public AuditedLoanRepositoryProxy(LoanRepository delegate, Path auditFile) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate null");
        }
        if (auditFile == null) {
            throw new IllegalArgumentException("auditFile null");
        }
        this.delegate = delegate;
        this.auditFile = auditFile;
    }

    @Override
    public synchronized void save(Loan loan) {
        delegate.save(loan);
        appendAuditLine(loan);
    }

    @Override
    public Optional<Loan> findById(String loanId) {
        return delegate.findById(loanId);
    }

    @Override
    public Optional<Loan> findActiveLoanByItemId(String itemId) {
        return delegate.findActiveLoanByItemId(itemId);
    }

    @Override
    public List<Loan> findByMemberId(String memberId) {
        return delegate.findByMemberId(memberId);
    }

    private void appendAuditLine(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }

        String eventType = loan.isActive() ? "BORROW" : "RETURN";
        String line = String.join("|",
                LocalDateTime.now().toString(),
                eventType,
                loan.getLoanId(),
                loan.getMemberId(),
                loan.getItemId());

        Path parent = auditFile.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                    auditFile,
                    line + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.WRITE);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to append audit line: " + auditFile, ex);
        }
    }
}
