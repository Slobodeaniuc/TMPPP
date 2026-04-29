package com.chiril.library.domain;

import com.chiril.library.flyweight.AuthorFlyweight;
import com.chiril.library.flyweight.AuthorFlyweightFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class AuthorFlyweightFactoryTest {

    @BeforeEach
    void setupPool() {
        AuthorFlyweightFactory.clear();
    }

    @AfterEach
    void cleanPool() {
        AuthorFlyweightFactory.clear();
    }

    @Test
    void getFlyweight_returnsSameReferenceForSameAuthor() {
        AuthorFlyweight first = AuthorFlyweightFactory.getFlyweight("Robert C. Martin");
        AuthorFlyweight second = AuthorFlyweightFactory.getFlyweight("  robert c. martin  ");

        assertSame(first, second);
        assertEquals(1, AuthorFlyweightFactory.poolSize());
    }

    @Test
    void booksWithSameAuthor_shareAuthorFlyweight() {
        Book first = Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build();

        Book second = Book.builder()
                .id("B2")
                .title("The Clean Coder")
                .author("Robert C. Martin")
                .isbn("978-0137081073")
                .build();

        assertSame(first.authorFlyweight(), second.authorFlyweight());
        assertEquals(1, AuthorFlyweightFactory.poolSize());
    }
}
