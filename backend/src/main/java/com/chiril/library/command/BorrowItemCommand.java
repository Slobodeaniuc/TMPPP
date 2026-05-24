package com.chiril.library.command;

import com.chiril.library.domain.Loan;
import com.chiril.library.facade.LibraryFacade;

public final class BorrowItemCommand implements LibraryCommand<Loan> {
    private final LibraryFacade facade;
    private final String memberId;
    private final String itemId;

    public BorrowItemCommand(LibraryFacade facade, String memberId, String itemId) {
        if (facade == null) {
            throw new IllegalArgumentException("facade null");
        }
        this.facade = facade;
        this.memberId = memberId;
        this.itemId = itemId;
    }

    @Override
    public Loan execute() {
        return facade.borrowItem(memberId, itemId);
    }
}
