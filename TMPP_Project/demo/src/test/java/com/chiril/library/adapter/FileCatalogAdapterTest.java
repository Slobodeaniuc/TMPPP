package com.chiril.library.adapter;

import com.chiril.library.adapter.storage.FileStorage;
import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;
import com.chiril.library.domain.Magazine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileCatalogAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    void addItem_persistsDataAcrossAdapterInstances() {
        Path storageFile = tempDir.resolve("catalog.db");
        FileCatalogAdapter writer = new FileCatalogAdapter(new FileStorage(storageFile));

        writer.addItem(Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build());

        FileCatalogAdapter reader = new FileCatalogAdapter(new FileStorage(storageFile));
        Optional<LibraryItem> loaded = reader.findById("B1");

        assertTrue(loaded.isPresent());
        Book loadedBook = assertInstanceOf(Book.class, loaded.get());
        assertEquals("Clean Code", loadedBook.getTitle());
        assertEquals("Robert C. Martin", loadedBook.getAuthor());
        assertEquals("978-0132350884", loadedBook.getIsbn());
    }

    @Test
    void getAllItems_loadsAllSupportedTypes() {
        Path storageFile = tempDir.resolve("catalog.db");
        FileCatalogAdapter catalog = new FileCatalogAdapter(new FileStorage(storageFile));

        catalog.addItem(Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build());

        catalog.addItem(Magazine.builder()
                .id("M1")
                .title("National Geographic")
                .issueNumber(202)
                .build());

        catalog.addItem(Dvd.builder()
                .id("D1")
                .title("Interstellar")
                .durationMinutes(169)
                .build());

        List<LibraryItem> allItems = catalog.getAllItems();
        assertEquals(3, allItems.size());
        assertTrue(allItems.stream().anyMatch(item -> "BOOK".equals(item.getType())));
        assertTrue(allItems.stream().anyMatch(item -> "MAGAZINE".equals(item.getType())));
        assertTrue(allItems.stream().anyMatch(item -> "DVD".equals(item.getType())));
    }

    @Test
    void addItem_persistsCompositeHierarchyAcrossAdapterInstances() {
        Path storageFile = tempDir.resolve("catalog.db");
        FileCatalogAdapter writer = new FileCatalogAdapter(new FileStorage(storageFile));

        LibraryItemGroup nestedGroup = LibraryItemGroup.builder()
                .id("G2")
                .title("Reading Bundle")
                .child(Magazine.builder()
                        .id("M1")
                        .title("National Geographic")
                        .issueNumber(202)
                        .build())
                .build();

        LibraryItemGroup rootGroup = LibraryItemGroup.builder()
                .id("G1")
                .title("Starter Kit")
                .child(Book.builder()
                        .id("B1")
                        .title("Clean Code")
                        .author("Robert C. Martin")
                        .isbn("978-0132350884")
                        .build())
                .child(nestedGroup)
                .build();

        writer.addItem(rootGroup);

        FileCatalogAdapter reader = new FileCatalogAdapter(new FileStorage(storageFile));
        LibraryItemGroup loadedGroup = assertInstanceOf(LibraryItemGroup.class, reader.findById("G1").orElseThrow());

        assertEquals(2, loadedGroup.getChildren().size());
        assertInstanceOf(Book.class, loadedGroup.getChildren().get(0));

        LibraryItemGroup loadedNested = assertInstanceOf(LibraryItemGroup.class, loadedGroup.getChildren().get(1));
        assertEquals(1, loadedNested.getChildren().size());
        assertInstanceOf(Magazine.class, loadedNested.getChildren().get(0));
    }
}
