package com.chiril.library.command;

import com.chiril.library.domain.Loan;
import com.chiril.library.domain.ReturnReceipt;
import com.chiril.library.facade.LibraryFacade;
import com.chiril.library.factory.StandardLibraryFactory;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LibraryCommandTest {

    private LibraryFacade facade;
    private LibraryCommandInvoker invoker;

    @BeforeEach
    void setUp() {
        facade = new LibraryFacade(new TestCatalog(), new TestLoanRepository(), new StandardLibraryFactory());
        invoker = new LibraryCommandInvoker();
        facade.addBook("B1", "Clean Code", "Robert C. Martin", "978-0132350884");
    }

    @Test
    void borrowAndReturnCommands_delegateToFacade() {
        Loan loan = invoker.submit(new BorrowItemCommand(facade, "U1", "B1"));
        ReturnReceipt receipt = invoker.submit(new ReturnItemWithPenaltyCommand(facade, "B1"));

        assertEquals("B1", loan.getItemId());
        assertEquals(loan.getLoanId(), receipt.getLoan().getLoanId());
        assertFalse(receipt.getLoan().isActive());
    }

    @Test
    void calculatePenaltyCommand_returnsFacadePenalty() {
        Loan loan = invoker.submit(new BorrowItemCommand(facade, "U1", "B1"));

        BigDecimal penalty = invoker.submit(new CalculatePenaltyCommand(
                facade,
                loan.getLoanId(),
                loan.getDueDate().plusDays(3)));

        assertEquals(BigDecimal.valueOf(4.50), penalty);
        assertEquals(2, invoker.history().size());
    }

    private static final class TestCatalog implements Catalog {
        private final Map<String, com.chiril.library.domain.LibraryItem> items = new LinkedHashMap<>();

        @Override
        public void addItem(com.chiril.library.domain.LibraryItem item) {
            items.put(item.getId(), item);
        }

        @Override
        public Optional<com.chiril.library.domain.LibraryItem> findById(String id) {
            return Optional.ofNullable(items.get(id));
        }

        @Override
        public List<com.chiril.library.domain.LibraryItem> getAllItems() {
            return items.values().stream().toList();
        }
    }

    private static final class TestLoanRepository implements LoanRepository {
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
