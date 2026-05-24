package com.chiril.library.dto;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;
import com.chiril.library.domain.Magazine;

import java.util.List;

public record LibraryItemDto(
        String id,
        String title,
        String type,
        String author,
        String isbn,
        Integer issueNumber,
        Integer durationMinutes,
        List<String> childIds
) {
    public static LibraryItemDto from(LibraryItem item) {
        if (item instanceof Book book) {
            return new LibraryItemDto(book.getId(), book.getTitle(), "BOOK",
                    book.getAuthor(), book.getIsbn(), null, null, null);
        }
        if (item instanceof Magazine mag) {
            return new LibraryItemDto(mag.getId(), mag.getTitle(), "MAGAZINE",
                    null, null, mag.getIssueNumber(), null, null);
        }
        if (item instanceof Dvd dvd) {
            return new LibraryItemDto(dvd.getId(), dvd.getTitle(), "DVD",
                    null, null, null, dvd.getDurationMinutes(), null);
        }
        if (item instanceof LibraryItemGroup group) {
            List<String> childIds = group.getChildren().stream()
                    .map(LibraryItem::getId)
                    .toList();
            return new LibraryItemDto(group.getId(), group.getTitle(), "GROUP",
                    null, null, null, null, childIds);
        }
        throw new IllegalArgumentException("Unknown item type: " + item.getClass().getSimpleName());
    }
}
