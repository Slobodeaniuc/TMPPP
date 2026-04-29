package com.chiril.library.factory;

import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;

public final class DvdCreator extends LibraryItemCreator {
    @Override
    protected LibraryItem createItem(ItemRequest r) {
        if (r.getDurationMinutes() == null || r.getDurationMinutes() <= 0)
            throw new IllegalArgumentException("durationMinutes invalid");
        return Dvd.builder()
                .id(r.getId())
                .title(r.getTitle())
                .durationMinutes(r.getDurationMinutes())
                .build();
    }
}
