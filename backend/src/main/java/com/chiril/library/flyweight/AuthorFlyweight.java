package com.chiril.library.flyweight;

public final class AuthorFlyweight {
    private final String authorName;

    public AuthorFlyweight(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            throw new IllegalArgumentException("authorName invalid");
        }
        this.authorName = authorName;
    }

    public String name() {
        return authorName;
    }
}