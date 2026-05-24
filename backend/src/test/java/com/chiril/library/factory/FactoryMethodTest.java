package com.chiril.library.factory;

import org.junit.jupiter.api.Test;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;

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

    @Test
    void groupCreator_createsCompositeGroup() {
        LibraryItemCreator creator = new GroupCreator();

        LibraryItem book = Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build();

        LibraryItem item = creator.create(
                ItemRequest.builder(ItemType.GROUP, "G1", "Starter Bundle")
                        .child(book)
                        .child(Dvd.builder()
                                .id("D1")
                                .title("Interstellar")
                                .durationMinutes(169)
                                .build())
                        .build());

        LibraryItemGroup group = assertInstanceOf(LibraryItemGroup.class, item);
        assertEquals(2, group.getChildren().size());
    }

    @Test
    void groupCreator_missingChildren_throws() {
        LibraryItemCreator creator = new GroupCreator();

        assertThrows(IllegalArgumentException.class,
                () -> creator.create(ItemRequest.builder(ItemType.GROUP, "G1", "Starter Bundle").build()));
    }
}
