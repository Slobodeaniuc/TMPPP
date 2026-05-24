package com.chiril.library.factory;

import com.chiril.library.domain.LibraryItem;

import java.util.ArrayList;
import java.util.List;

public final class ItemRequest {
    private final ItemType type;
    private final String id;
    private final String title;

    // opționale, în funcție de tip
    private final String author;
    private final String isbn;
    private final Integer issueNumber;
    private final Integer durationMinutes;
    private final List<LibraryItem> children;

    private ItemRequest(Builder b) {
        this.type = b.type;
        this.id = b.id;
        this.title = b.title;
        this.author = b.author;
        this.isbn = b.isbn;
        this.issueNumber = b.issueNumber;
        this.durationMinutes = b.durationMinutes;
        this.children = List.copyOf(b.children);
    }

    public ItemType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getIssueNumber() {
        return issueNumber;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public List<LibraryItem> getChildren() {
        return children;
    }

    public static Builder builder(ItemType type, String id, String title) {
        return new Builder(type, id, title);
    }

    public static final class Builder {
        private final ItemType type;
        private final String id;
        private final String title;

        private String author;
        private String isbn;
        private Integer issueNumber;
        private Integer durationMinutes;
        private final List<LibraryItem> children = new ArrayList<>();

        private Builder(ItemType type, String id, String title) {
            this.type = type;
            this.id = id;
            this.title = title;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder issueNumber(int issueNumber) {
            this.issueNumber = issueNumber;
            return this;
        }

        public Builder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Builder child(LibraryItem child) {
            if (child == null) {
                throw new IllegalArgumentException("child null");
            }
            this.children.add(child);
            return this;
        }

        public Builder children(List<LibraryItem> children) {
            if (children == null) {
                throw new IllegalArgumentException("children null");
            }
            for (LibraryItem child : children) {
                child(child);
            }
            return this;
        }

        public ItemRequest build() {
            return new ItemRequest(this);
        }
    }
}
