package com.chiril.library.domain;

public final class Book extends LibraryItem {
    private final String author;
    private final String isbn;

    private Book(Builder builder) {
        super(builder.id, builder.title);
        if (builder.author == null || builder.author.trim().isEmpty())
            throw new IllegalArgumentException("author invalid");
        if (builder.isbn == null || builder.isbn.trim().isEmpty())
            throw new IllegalArgumentException("isbn invalid");
        this.author = builder.author;
        this.isbn = builder.isbn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public Book clone() {
        Builder builder = new Builder();
        builder.id(this.getId());
        builder.title(this.getTitle());
        builder.author(this.author);
        builder.isbn(this.isbn);
        return builder.build();
    }

    @Override
    public String getType() {
        return "BOOK";
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") +
                ", author='" + author + "', isbn='" + isbn + "'}";
    }

    public static final class Builder {
        private String id;
        private String title;
        private String author;
        private String isbn;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }
}