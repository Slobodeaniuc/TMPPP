package com.chiril.library.factory;

public final class ItemRequest {
    private final ItemType type;
    private final String id;
    private final String title;

    // opționale, în funcție de tip
    private final String author;
    private final String isbn;
    private final Integer issueNumber;
    private final Integer durationMinutes;

    private ItemRequest(Builder b) {
        this.type = b.type;
        this.id = b.id;
        this.title = b.title;
        this.author = b.author;
        this.isbn = b.isbn;
        this.issueNumber = b.issueNumber;
        this.durationMinutes = b.durationMinutes;
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

        public ItemRequest build() {
            return new ItemRequest(this);
        }
    }
}
