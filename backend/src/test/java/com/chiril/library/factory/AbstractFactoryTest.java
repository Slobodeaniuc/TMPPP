package com.chiril.library.factory;

import org.junit.jupiter.api.Test;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;
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

        LibraryItem group = factory.groupCreator().create(
                ItemRequest.builder(ItemType.GROUP, "G1", "Starter Bundle")
                        .child(item)
                        .build());
        assertTrue(group instanceof LibraryItemGroup);

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

        LibraryItem group = factory.groupCreator().create(
                ItemRequest.builder(ItemType.GROUP, "G1", "Starter Bundle")
                        .child(item)
                        .build());
        assertTrue(group instanceof LibraryItemGroup);
    }

        @Test
        void standardFactory_appliesDvdCapAndWeekendAdjustment() {
                LibraryAbstractFactory factory = new StandardLibraryFactory();

                Dvd dvd = Dvd.builder()
                                .id("D1")
                                .title("Interstellar")
                                .durationMinutes(169)
                                .build();

                // 2026-03-01 + 7 zile = duminica 2026-03-08, apoi ajustare la luni 2026-03-09
                LocalDate due = factory.loanPolicy().computeDueDate(dvd, LocalDate.of(2026, 3, 1));
                assertEquals(LocalDate.of(2026, 3, 9), due);
        }
}
