package com.chiril.library.memento;

import java.util.List;

public final class BorrowCartMemento {
    private final List<String> itemIds;

    public BorrowCartMemento(List<String> itemIds) {
        if (itemIds == null) {
            throw new IllegalArgumentException("itemIds null");
        }
        this.itemIds = List.copyOf(itemIds);
    }

    List<String> itemIds() {
        return itemIds;
    }
}
