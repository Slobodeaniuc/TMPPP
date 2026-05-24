package com.chiril.library.domain;

import com.chiril.library.flyweight.AuthorFlyweight;
import com.chiril.library.flyweight.AuthorFlyweightFactory;

public final class Book extends LibraryItem {
    private final AuthorFlyweight authorFlyweight;
    private final String isbn;

    private Book(Builder builder) {
        super(builder.id, builder.title);
        if (builder.author == null || builder.author.isBlank())
            throw new IllegalArgumentException("author invalid");
        if (builder.isbn == null || builder.isbn.isBlank())
            throw new IllegalArgumentException("isbn invalid");
        this.authorFlyweight = AuthorFlyweightFactory.getFlyweight(builder.author);
        this.isbn = builder.isbn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAuthor() {
        return authorFlyweight.name();
    }

    public String getIsbn() {
        return isbn;
    }

    AuthorFlyweight authorFlyweight() {
        return authorFlyweight;
    }

    @Override
    public Book clone() {
        Builder builder = new Builder();
        builder.id(this.getId());
        builder.title(this.getTitle());
        builder.author(this.getAuthor());
        builder.isbn(this.getIsbn());
        return builder.build();
    }

    @Override
    public String getType() {
        return "BOOK";
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") +
                ", author='" + getAuthor() + "', isbn='" + getIsbn() + "'}";
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