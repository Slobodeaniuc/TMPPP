package com.chiril.library.factory;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.service.LoanPolicy;

import java.time.LocalDate;

public final class ShortLoanLibraryFactory implements LibraryAbstractFactory {

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
        // policy “din fabrică” (familie)
        return new LoanPolicy() {
            @Override
            public LocalDate computeDueDate(LibraryItem item, LocalDate loanDate) {
                // minim: împrumut mai scurt pentru toate
                return loanDate.plusDays(7);
            }
        };
    }
}
