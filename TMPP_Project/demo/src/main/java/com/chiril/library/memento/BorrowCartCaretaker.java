package com.chiril.library.memento;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class BorrowCartCaretaker {
    private final Deque<BorrowCartMemento> history = new ArrayDeque<>();

    public void push(BorrowCartMemento memento) {
        if (memento == null) {
            throw new IllegalArgumentException("memento null");
        }
        history.push(memento);
    }

    public Optional<BorrowCartMemento> pop() {
        if (history.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(history.pop());
    }

    public void clear() {
        history.clear();
    }
}
