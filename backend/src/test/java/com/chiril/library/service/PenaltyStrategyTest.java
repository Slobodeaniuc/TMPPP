package com.chiril.library.service;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.Loan;
import com.chiril.library.domain.Magazine;
import com.chiril.library.service.penalty.GracePeriodPenaltyStrategy;
import com.chiril.library.service.penalty.ItemTypePenaltyStrategy;
import com.chiril.library.service.penalty.PenaltyStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PenaltyStrategyTest {

    @Test
    void itemTypeStrategy_computesBookPenaltyPerOverdueDay() {
        PenaltyStrategy strategy = new ItemTypePenaltyStrategy();
        Book book = Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build();
        Loan loan = new Loan("L1", "U1", "B1", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15));

        BigDecimal penalty = strategy.computePenalty(book, loan, LocalDate.of(2026, 3, 18));

        assertEquals(BigDecimal.valueOf(4.50), penalty);
    }

    @Test
    void itemTypeStrategy_usesDifferentRatesForDvdAndMagazine() {
        PenaltyStrategy strategy = new ItemTypePenaltyStrategy();
        Dvd dvd = Dvd.builder()
                .id("D1")
                .title("Interstellar")
                .durationMinutes(169)
                .build();
        Magazine magazine = Magazine.builder()
                .id("M1")
                .title("National Geographic")
                .issueNumber(202)
                .build();
        Loan dvdLoan = new Loan("L1", "U1", "D1", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 8));
        Loan magazineLoan = new Loan("L2", "U1", "M1", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 8));

        assertEquals(BigDecimal.valueOf(6.00), strategy.computePenalty(dvd, dvdLoan, LocalDate.of(2026, 3, 10)));
        assertEquals(BigDecimal.valueOf(2.00), strategy.computePenalty(magazine, magazineLoan, LocalDate.of(2026, 3, 10)));
    }

    @Test
    void gracePeriodStrategy_skipsFirstTwoOverdueDays() {
        PenaltyStrategy strategy = new GracePeriodPenaltyStrategy(new ItemTypePenaltyStrategy(), 2);
        Book book = Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build();
        Loan loan = new Loan("L1", "U1", "B1", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15));

        assertEquals(BigDecimal.ZERO, strategy.computePenalty(book, loan, LocalDate.of(2026, 3, 17)));
        assertEquals(BigDecimal.valueOf(1.50), strategy.computePenalty(book, loan, LocalDate.of(2026, 3, 18)));
    }
}
