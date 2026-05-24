package com.chiril.library.iterator;

import com.chiril.library.domain.LibraryItem;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class FilteredLibraryItemIterator implements LibraryIterator<LibraryItem> {
    private final Iterator<LibraryItem> source;
    private final LibraryItemCriteria criteria;
    private LibraryItem nextItem;

    public FilteredLibraryItemIterator(List<LibraryItem> items, LibraryItemCriteria criteria) {
        if (items == null) {
            throw new IllegalArgumentException("items null");
        }
        if (criteria == null) {
            throw new IllegalArgumentException("criteria null");
        }
        this.source = items.iterator();
        this.criteria = criteria;
        advance();
    }

    @Override
    public boolean hasNext() {
        return nextItem != null;
    }

    @Override
    public LibraryItem next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more matching items");
        }
        LibraryItem current = nextItem;
        advance();
        return current;
    }

    private void advance() {
        nextItem = null;
        while (source.hasNext()) {
            LibraryItem candidate = source.next();
            if (criteria.matches(candidate)) {
                nextItem = candidate;
                return;
            }
        }
    }
}
