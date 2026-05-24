package com.chiril.library.factory;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Magazine;

public final class MagazineCreator extends LibraryItemCreator {
    @Override
    protected LibraryItem createItem(ItemRequest r) {
        if (r.getIssueNumber() == null || r.getIssueNumber() <= 0)
            throw new IllegalArgumentException("issueNumber invalid");
        return Magazine.builder()
                .id(r.getId())
                .title(r.getTitle())
                .issueNumber(r.getIssueNumber())
                .build();
    }
}
