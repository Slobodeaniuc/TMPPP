package com.chiril.library.repo;

import java.util.List;
import java.util.Optional;

import com.chiril.library.domain.LibraryItem;

public interface Catalog {
    void addItem(LibraryItem item); // add item in catalog
    Optional<LibraryItem> findById(String id);
    List<LibraryItem> getAllItems();
}