package com.chiril.library.observer;

import com.chiril.library.domain.Loan;

public interface LibraryObserver {
    default void onItemBorrowed(Loan loan) {
    }

    default void onItemReturned(Loan loan) {
    }
}
