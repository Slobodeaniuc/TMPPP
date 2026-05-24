package com.chiril.library.observer;

import com.chiril.library.domain.Loan;

import java.util.ArrayList;
import java.util.List;

public final class LibraryEventPublisher {
    private final List<LibraryObserver> observers = new ArrayList<>();

    public synchronized void register(LibraryObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer null");
        }
        observers.add(observer);
    }

    public synchronized void unregister(LibraryObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer null");
        }
        observers.remove(observer);
    }

    public synchronized void notifyItemBorrowed(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        for (LibraryObserver observer : List.copyOf(observers)) {
            observer.onItemBorrowed(loan);
        }
    }

    public synchronized void notifyItemReturned(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        for (LibraryObserver observer : List.copyOf(observers)) {
            observer.onItemReturned(loan);
        }
    }
}
