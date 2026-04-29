package com.chiril.library.flyweight;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class AuthorFlyweightFactory {
    private static final ConcurrentMap<String, AuthorFlyweight> POOL = new ConcurrentHashMap<>();

    private AuthorFlyweightFactory() {
    }

    public static AuthorFlyweight getFlyweight(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            throw new IllegalArgumentException("authorName invalid");
        }

        String normalized = normalize(authorName);
        return POOL.computeIfAbsent(normalized, key -> new AuthorFlyweight(authorName.trim()));
    }

    public static int poolSize() {
        return POOL.size();
    }

    public static void clear() {
        POOL.clear();
    }

    private static String normalize(String authorName) {
        return authorName.trim().toLowerCase();
    }
}