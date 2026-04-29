package com.chiril.library.command;

import com.chiril.library.domain.ReturnReceipt;
import com.chiril.library.facade.LibraryFacade;

public final class ReturnItemWithPenaltyCommand implements LibraryCommand<ReturnReceipt> {
    private final LibraryFacade facade;
    private final String itemId;

    public ReturnItemWithPenaltyCommand(LibraryFacade facade, String itemId) {
        if (facade == null) {
            throw new IllegalArgumentException("facade null");
        }
        this.facade = facade;
        this.itemId = itemId;
    }

    @Override
    public ReturnReceipt execute() {
        return facade.returnItemWithPenalty(itemId);
    }
}
