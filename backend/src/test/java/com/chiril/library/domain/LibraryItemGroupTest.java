package com.chiril.library.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryItemGroupTest {

    @Test
    void flattenLeafItems_supportsNestedGroups() {
        LibraryItem book = Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build();

        LibraryItem dvd = Dvd.builder()
                .id("D1")
                .title("Interstellar")
                .durationMinutes(169)
                .build();

        LibraryItem magazine = Magazine.builder()
                .id("M1")
                .title("National Geographic")
                .issueNumber(202)
                .build();

        LibraryItemGroup nestedGroup = LibraryItemGroup.builder()
                .id("G2")
                .title("Reading Bundle")
                .child(magazine)
                .build();

        LibraryItemGroup rootGroup = LibraryItemGroup.builder()
                .id("G1")
                .title("Starter Kit")
                .child(book)
                .child(nestedGroup)
                .child(dvd)
                .build();

        assertEquals(3, rootGroup.flattenLeafItems().size());
        assertTrue(rootGroup.flattenLeafItems().stream().anyMatch(item -> "B1".equals(item.getId())));
        assertTrue(rootGroup.flattenLeafItems().stream().anyMatch(item -> "M1".equals(item.getId())));
        assertTrue(rootGroup.flattenLeafItems().stream().anyMatch(item -> "D1".equals(item.getId())));
    }

    @Test
    void addChild_rejectsDuplicateChildIds() {
        LibraryItemGroup group = LibraryItemGroup.builder()
                .id("G1")
                .title("Starter Kit")
                .build();

        group.addChild(Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build());

        assertThrows(IllegalArgumentException.class, () -> group.addChild(Book.builder()
                .id("B1")
                .title("The Clean Coder")
                .author("Robert C. Martin")
                .isbn("978-0137081073")
                .build()));
    }

    @Test
    void addChild_rejectsCycles() {
        LibraryItemGroup parent = LibraryItemGroup.builder()
                .id("G1")
                .title("Parent")
                .build();

        LibraryItemGroup child = LibraryItemGroup.builder()
                .id("G2")
                .title("Child")
                .build();

        parent.addChild(child);

        assertThrows(IllegalArgumentException.class, () -> child.addChild(parent));
    }

    @Test
    void clone_createsDeepCopyForNestedGroups() {
        LibraryItemGroup nested = LibraryItemGroup.builder()
                .id("G2")
                .title("Nested")
                .child(Book.builder()
                        .id("B1")
                        .title("Clean Code")
                        .author("Robert C. Martin")
                        .isbn("978-0132350884")
                        .build())
                .build();

        LibraryItemGroup original = LibraryItemGroup.builder()
                .id("G1")
                .title("Root")
                .child(nested)
                .build();

        LibraryItemGroup cloned = original.clone();

        LibraryItemGroup originalNested = (LibraryItemGroup) original.getChildren().get(0);
        assertTrue(originalNested.removeChildById("B1"));

        LibraryItemGroup clonedNested = (LibraryItemGroup) cloned.getChildren().get(0);
        assertEquals(1, clonedNested.getChildren().size());
        assertFalse(clonedNested.removeChildById("B2"));
    }
}
