package com.chiril.library.factory;

import org.junit.jupiter.api.Test;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.LibraryItem;

import static org.junit.jupiter.api.Assertions.*;

public class FactoryMethodTest {

    @Test
    void bookCreator_createsBook() {
        LibraryItemCreator creator = new BookCreator();

        LibraryItem item = creator.create(
                ItemRequest.builder(ItemType.BOOK, "B1", "Clean Code")
                        .author("Robert C. Martin")
                        .isbn("978-0132350884")
                        .build());

        assertTrue(item instanceof Book);
        assertEquals("B1", item.getId());
        assertEquals("Clean Code", item.getTitle());
    }

    @Test
    void dvdCreator_missingDuration_throws() {
        LibraryItemCreator creator = new DvdCreator();

        assertThrows(IllegalArgumentException.class,
                () -> creator.create(ItemRequest.builder(ItemType.DVD, "D1", "Interstellar").build()));
    }
}
