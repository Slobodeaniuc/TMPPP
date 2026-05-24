package com.chiril.library.repo;

import java.util.*;

import com.chiril.library.domain.LibraryItem;

public final class InMemoryCatalog implements Catalog {
    private static InMemoryCatalog instance;
    private final Map<String, LibraryItem> items = new HashMap<>();

    private InMemoryCatalog() {
    }

    public static synchronized InMemoryCatalog getInstance() {
        if (instance == null) {
            instance = new InMemoryCatalog();
        }
        return instance;
    }

    @Override
    public void addItem(LibraryItem item) {
        if (item == null)
            throw new IllegalArgumentException("item null");
        items.put(item.getId(), item);
    }

    @Override
    public Optional<LibraryItem> findById(String id) {
        if (id == null || id.isBlank())
            return Optional.empty();
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<LibraryItem> getAllItems() {
        return new ArrayList<>(items.values());
    }
}