package com.chiril.library.iterator;

import com.chiril.library.domain.LibraryItem;

@FunctionalInterface
public interface LibraryItemCriteria {
    boolean matches(LibraryItem item);
}
