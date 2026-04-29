package com.chiril.library.adapter;

import com.chiril.library.adapter.storage.FileStorage;
import com.chiril.library.domain.Loan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileLoanRepositoryAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndFindById_preservesReturnedLoanState() {
        Path storageFile = tempDir.resolve("loans.db");
        FileLoanRepositoryAdapter writer = new FileLoanRepositoryAdapter(new FileStorage(storageFile));

        Loan loan = new Loan(
                "L1",
                "U1",
                "B1",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 24));

        writer.save(loan);
        loan.markReturned(LocalDate.of(2026, 3, 11));
        writer.save(loan);

        FileLoanRepositoryAdapter reader = new FileLoanRepositoryAdapter(new FileStorage(storageFile));
        Loan loadedLoan = reader.findById("L1").orElseThrow();

        assertFalse(loadedLoan.isActive());
        assertEquals(LocalDate.of(2026, 3, 11), loadedLoan.getReturnDate());
    }

    @Test
    void findActiveLoanByItemId_returnsOnlyActiveLoan() {
        Path storageFile = tempDir.resolve("loans.db");
        FileLoanRepositoryAdapter repository = new FileLoanRepositoryAdapter(new FileStorage(storageFile));

        Loan returnedLoan = new Loan(
                "L1",
                "U1",
                "B1",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15));
        returnedLoan.markReturned(LocalDate.of(2026, 3, 2));
        repository.save(returnedLoan);

        Loan activeLoan = new Loan(
                "L2",
                "U2",
                "B1",
                LocalDate.of(2026, 3, 3),
                LocalDate.of(2026, 3, 17));
        repository.save(activeLoan);

        Optional<Loan> result = repository.findActiveLoanByItemId("B1");
        assertTrue(result.isPresent());
        assertEquals("L2", result.get().getLoanId());
    }

    @Test
    void findByMemberId_returnsOnlyMemberLoans() {
        Path storageFile = tempDir.resolve("loans.db");
        FileLoanRepositoryAdapter repository = new FileLoanRepositoryAdapter(new FileStorage(storageFile));

        repository.save(new Loan(
                "L1",
                "U1",
                "B1",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15)));

        repository.save(new Loan(
                "L2",
                "U2",
                "B2",
                LocalDate.of(2026, 3, 2),
                LocalDate.of(2026, 3, 16)));

        repository.save(new Loan(
                "L3",
                "U1",
                "B3",
                LocalDate.of(2026, 3, 3),
                LocalDate.of(2026, 3, 17)));

        List<Loan> u1Loans = repository.findByMemberId("U1");
        assertEquals(2, u1Loans.size());
        assertTrue(u1Loans.stream().allMatch(loan -> "U1".equals(loan.getMemberId())));
    }
}
