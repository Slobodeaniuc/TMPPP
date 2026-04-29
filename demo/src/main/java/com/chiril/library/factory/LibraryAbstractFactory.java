package com.chiril.library.factory;

import com.chiril.library.service.LoanPolicy;

public interface LibraryAbstractFactory {
    LibraryItemCreator bookCreator();

    LibraryItemCreator magazineCreator();

    LibraryItemCreator dvdCreator();

    LoanPolicy loanPolicy(); // obiect înrudit (familie)
}