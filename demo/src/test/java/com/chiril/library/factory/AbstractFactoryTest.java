package com.chiril.library.factory;

import org.junit.jupiter.api.Test;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.service.LoanPolicy;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractFactoryTest {

    @Test
    void standardFactory_createsCreatorsAndPolicy() {
        LibraryAbstractFactory factory = new StandardLibraryFactory();

        LibraryItem item = factory.bookCreator().create(
                ItemRequest.builder(ItemType.BOOK, "B1", "Clean Code")
                        .author("Robert C. Martin")
                        .isbn("978-0132350884")
                        .build());

        assertTrue(item instanceof Book);

        LoanPolicy policy = factory.loanPolicy();
        LocalDate due = policy.computeDueDate(item, LocalDate.of(2026, 2, 3));
        assertEquals(LocalDate.of(2026, 2, 17), due); // 14 zile
    }

    @Test
    void shortLoanFactory_hasDifferentPolicy() {
        LibraryAbstractFactory factory = new ShortLoanLibraryFactory();

        LibraryItem item = factory.dvdCreator().create(
                ItemRequest.builder(ItemType.DVD, "D1", "Interstellar")
                        .durationMinutes(169)
                        .build());

        LocalDate due = factory.loanPolicy().computeDueDate(item, LocalDate.of(2026, 2, 3));
        assertEquals(LocalDate.of(2026, 2, 10), due); // 7 zile
    }
}
