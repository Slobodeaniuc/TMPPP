package com.chiril.library.service;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;
import com.chiril.library.notification.NotificationChannel;
import com.chiril.library.observer.PenaltyObserver;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;
import com.chiril.library.service.penalty.ItemTypePenaltyStrategy;
import com.chiril.library.service.penalty.PenaltyService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PenaltyObserverTest {

    @Test
    void penaltyObserver_notifiesWhenReturnedLoanIsOverdue() {
        TestCatalog catalog = new TestCatalog();
        TestLoanRepository loans = new TestLoanRepository();
        CapturingChannel channel = new CapturingChannel();

        catalog.addItem(Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build());

        Loan overdueLoan = new Loan(
                "L1",
                "U1",
                "B1",
                java.time.LocalDate.now().minusDays(20),
                java.time.LocalDate.now().minusDays(6));
        loans.save(overdueLoan);

        LibraryService service = new LibraryService(catalog, loans, new DefaultLoanPolicy());
        service.registerObserver(new PenaltyObserver(
                new PenaltyService(catalog, loans, new ItemTypePenaltyStrategy()),
                channel));

        service.returnItem("B1");

        assertEquals(1, channel.messages.size());
        assertTrue(channel.messages.get(0).contains("[U1] Penalty for item B1 is 9.0"));
    }

    private static final class CapturingChannel implements NotificationChannel {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void send(String memberId, String message) {
            messages.add("[" + memberId + "] " + message);
        }
    }

    private static final class TestCatalog implements Catalog {
        private final Map<String, LibraryItem> items = new LinkedHashMap<>();

        @Override
        public void addItem(LibraryItem item) {
            items.put(item.getId(), item);
        }

        @Override
        public Optional<LibraryItem> findById(String id) {
            return Optional.ofNullable(items.get(id));
        }

        @Override
        public List<LibraryItem> getAllItems() {
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
