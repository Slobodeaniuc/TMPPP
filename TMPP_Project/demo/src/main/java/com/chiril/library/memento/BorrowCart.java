package com.chiril.library.memento;

import java.util.ArrayList;
import java.util.List;

public final class BorrowCart {
    private final List<String> itemIds = new ArrayList<>();

    public void addItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId invalid");
        }
        if (itemIds.contains(itemId)) {
            throw new IllegalArgumentException("Item already present in cart: " + itemId);
        }
        itemIds.add(itemId);
    }

    public boolean removeItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return false;
        }
        return itemIds.remove(itemId);
    }

    public void clear() {
        itemIds.clear();
    }

    public List<String> getItemIds() {
        return List.copyOf(itemIds);
    }

    public BorrowCartMemento save() {
        return new BorrowCartMemento(itemIds);
    }

    public void restore(BorrowCartMemento memento) {
        if (memento == null) {
            throw new IllegalArgumentException("memento null");
        }
        itemIds.clear();
        itemIds.addAll(memento.itemIds());
    }
}
