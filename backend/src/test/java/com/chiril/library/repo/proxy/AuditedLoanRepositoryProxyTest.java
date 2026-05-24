package com.chiril.library.repo.proxy;

import com.chiril.library.domain.Loan;
import com.chiril.library.repo.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuditedLoanRepositoryProxyTest {

    @TempDir
    Path tempDir;

    @Test
    void save_writesBorrowAndReturnAuditEvents() throws Exception {
        Path auditFile = tempDir.resolve("loan-audit.log");
        LoanRepository proxy = new AuditedLoanRepositoryProxy(new InMemoryLoanRepositoryStub(), auditFile);

        Loan loan = new Loan("L1", "U1", "B1", LocalDate.of(2026, 3, 25), LocalDate.of(2026, 4, 8));

        proxy.save(loan);
        loan.markReturned(LocalDate.of(2026, 3, 26));
        proxy.save(loan);

        List<String> lines = Files.readAllLines(auditFile);

        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("|BORROW|L1|U1|B1"));
        assertTrue(lines.get(1).contains("|RETURN|L1|U1|B1"));
    }

    @Test
    void find_methods_delegate_to_wrapped_repository() {
        InMemoryLoanRepositoryStub delegate = new InMemoryLoanRepositoryStub();
        LoanRepository proxy = new AuditedLoanRepositoryProxy(delegate, tempDir.resolve("loan-audit.log"));

        Loan loan = new Loan("L1", "U1", "B1", LocalDate.of(2026, 3, 25), LocalDate.of(2026, 4, 8));
        delegate.save(loan);

        Optional<Loan> byId = proxy.findById("L1");
        Optional<Loan> active = proxy.findActiveLoanByItemId("B1");
        List<Loan> byMember = proxy.findByMemberId("U1");

        assertTrue(byId.isPresent());
        assertTrue(active.isPresent());
        assertEquals(1, byMember.size());
    }

    private static final class InMemoryLoanRepositoryStub implements LoanRepository {
        private final Map<String, Loan> loans = new LinkedHashMap<>();

        @Override
        public void save(Loan loan) {
            loans.put(loan.getLoanId(), loan);
        }

        @Override
        public Optional<Loan> findById(String loanId) {
            return Optional.ofNullable(loans.get(loanId));
        }

        @Override
        public Optional<Loan> findActiveLoanByItemId(String itemId) {
            return loans.values().stream()
                    .filter(loan -> itemId.equals(loan.getItemId()) && loan.isActive())
                    .findFirst();
        }

        @Override
        public List<Loan> findByMemberId(String memberId) {
            return loans.values().stream()
                    .filter(loan -> memberId.equals(loan.getMemberId()))
                    .toList();
        }
    }
}
