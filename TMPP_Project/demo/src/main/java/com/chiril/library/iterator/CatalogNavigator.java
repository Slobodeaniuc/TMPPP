package com.chiril.library.iterator;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;

import java.util.ArrayList;
import java.util.List;

public final class CatalogNavigator {
    private final Catalog catalog;
    private final LoanRepository loanRepository;

    public CatalogNavigator(Catalog catalog, LoanRepository loanRepository) {
        if (catalog == null) {
            throw new IllegalArgumentException("catalog null");
        }
        if (loanRepository == null) {
            throw new IllegalArgumentException("loanRepository null");
        }
        this.catalog = catalog;
        this.loanRepository = loanRepository;
    }

    public LibraryIterator<LibraryItem> iterateAvailableBooksByAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("author invalid");
        }
        String normalizedAuthor = author.trim();
        return new FilteredLibraryItemIterator(
                catalog.getAllItems(),
                item -> item instanceof Book book
                        && normalizedAuthor.equalsIgnoreCase(book.getAuthor())
                        && loanRepository.findActiveLoanByItemId(item.getId()).isEmpty());
    }

    public List<LibraryItem> collect(LibraryIterator<LibraryItem> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator null");
        }
        List<LibraryItem> items = new ArrayList<>();
        while (iterator.hasNext()) {
            items.add(iterator.next());
        }
        return items;
    }
}
