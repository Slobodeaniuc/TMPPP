package com.chiril.library.factory;

import com.chiril.library.service.DefaultLoanPolicy;
import com.chiril.library.service.LoanPolicy;

public final class StandardLibraryFactory implements LibraryAbstractFactory {

    @Override
    public LibraryItemCreator bookCreator() {
        return new BookCreator();
    }

    @Override
    public LibraryItemCreator magazineCreator() {
        return new MagazineCreator();
    }

    @Override
    public LibraryItemCreator dvdCreator() {
        return new DvdCreator();
    }

    @Override
    public LoanPolicy loanPolicy() {
        return new DefaultLoanPolicy();
    } // 14 zile (cum ai)
}