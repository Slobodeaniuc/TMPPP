package com.chiril.library.factory;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.LibraryItem;

public final class BookCreator extends LibraryItemCreator {
    @Override
    protected LibraryItem createItem(ItemRequest r) {
        if (r.getAuthor() == null || r.getAuthor().isBlank())
            throw new IllegalArgumentException("author invalid");
        if (r.getIsbn() == null || r.getIsbn().isBlank())
            throw new IllegalArgumentException("isbn invalid");
        return Book.builder()
                .id(r.getId())
                .title(r.getTitle())
                .author(r.getAuthor())
                .isbn(r.getIsbn())
                .build();
    }
}
