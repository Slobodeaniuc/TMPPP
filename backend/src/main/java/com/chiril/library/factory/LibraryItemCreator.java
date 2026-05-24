package com.chiril.library.factory;

import com.chiril.library.domain.LibraryItem;

public abstract class LibraryItemCreator {

    // metoda “template” (nu e obligatoriu, dar e curată)
    public final LibraryItem create(ItemRequest request) {
        if (request == null)
            throw new IllegalArgumentException("request null");
        if (request.getId() == null || request.getId().isBlank())
            throw new IllegalArgumentException("id invalid");
        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new IllegalArgumentException("title invalid");
        return createItem(request); // FACTORY METHOD
    }

    protected abstract LibraryItem createItem(ItemRequest request);
}
