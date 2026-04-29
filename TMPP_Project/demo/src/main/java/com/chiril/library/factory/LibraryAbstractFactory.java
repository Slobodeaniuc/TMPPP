package com.chiril.library.factory;

import com.chiril.library.service.LoanPolicy;
import com.chiril.library.service.penalty.PenaltyStrategy;

public interface LibraryAbstractFactory {
    LibraryItemCreator bookCreator();

    LibraryItemCreator magazineCreator();

    LibraryItemCreator dvdCreator();

    LibraryItemCreator groupCreator();

    LoanPolicy loanPolicy();

    PenaltyStrategy penaltyStrategy();
}
