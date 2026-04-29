package com.chiril.library.adapter;

import com.chiril.library.adapter.codec.LibraryItemRecordCodec;
import com.chiril.library.adapter.storage.FileStorage;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.repo.Catalog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FileCatalogAdapter implements Catalog {
    private final FileStorage storage;
    private final LibraryItemRecordCodec codec;

    public FileCatalogAdapter(FileStorage storage) {
        this(storage, new LibraryItemRecordCodec());
    }

    FileCatalogAdapter(FileStorage storage, LibraryItemRecordCodec codec) {
        if (storage == null) {
            throw new IllegalArgumentException("storage null");
        }
        if (codec == null) {
            throw new IllegalArgumentException("codec null");
        }
        this.storage = storage;
        this.codec = codec;
    }

    @Override
    public synchronized void addItem(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("item null");
        }
        Map<String, LibraryItem> items = loadItems();
        items.put(item.getId(), item);
        persistItems(items);
    }

    @Override
    public synchronized Optional<LibraryItem> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(loadItems().get(id));
    }

    @Override
    public synchronized List<LibraryItem> getAllItems() {
        List<LibraryItem> items = new ArrayList<>(loadItems().values());
        items.sort(Comparator.comparing(LibraryItem::getId));
        return items;
    }

    private Map<String, LibraryItem> loadItems() {
        Map<String, LibraryItem> items = new LinkedHashMap<>();
        for (String line : storage.readLines()) {
            if (line.isBlank()) {
                continue;
            }
            LibraryItem item = codec.decode(line);
            items.put(item.getId(), item);
        }
        return items;
    }

    private void persistItems(Map<String, LibraryItem> items) {
        List<String> lines = items.values().stream()
                .sorted(Comparator.comparing(LibraryItem::getId))
                .map(codec::encode)
                .toList();
        storage.writeLines(lines);
    }
}
