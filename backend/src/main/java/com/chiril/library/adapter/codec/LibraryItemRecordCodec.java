package com.chiril.library.adapter.codec;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;
import com.chiril.library.domain.Magazine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class LibraryItemRecordCodec {
    private static final String FIELD_SEPARATOR = "|";
    private static final String GROUP_CHILD_SEPARATOR = ",";
    private static final String TYPE_BOOK = "BOOK";
    private static final String TYPE_MAGAZINE = "MAGAZINE";
    private static final String TYPE_DVD = "DVD";
    private static final String TYPE_GROUP = "GROUP";

    public String encode(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("item null");
        }

        if (item instanceof Book book) {
            return encodeBook(book);
        }

        if (item instanceof Magazine magazine) {
            return encodeMagazine(magazine);
        }

        if (item instanceof Dvd dvd) {
            return encodeDvd(dvd);
        }

        if (item instanceof LibraryItemGroup group) {
            return encodeGroup(group);
        }

        throw new IllegalArgumentException("Unsupported item type: " + item.getClass().getSimpleName());
    }

    public LibraryItem decode(String record) {
        if (record == null || record.isBlank()) {
            throw new IllegalArgumentException("record invalid");
        }

        String[] parts = record.split("\\|", -1);
        String type = parts[0];

        return switch (type) {
            case TYPE_BOOK -> decodeBook(parts);
            case TYPE_MAGAZINE -> decodeMagazine(parts);
            case TYPE_DVD -> decodeDvd(parts);
            case TYPE_GROUP -> decodeGroup(parts);
            default -> throw new IllegalArgumentException("Unknown item type in record: " + type);
        };
    }

    private String encodeBook(Book book) {
        return String.join(FIELD_SEPARATOR,
                TYPE_BOOK,
                Base64Codec.encode(book.getId()),
                Base64Codec.encode(book.getTitle()),
                Base64Codec.encode(book.getAuthor()),
                Base64Codec.encode(book.getIsbn()));
    }

    private String encodeMagazine(Magazine magazine) {
        return String.join(FIELD_SEPARATOR,
                TYPE_MAGAZINE,
                Base64Codec.encode(magazine.getId()),
                Base64Codec.encode(magazine.getTitle()),
                String.valueOf(magazine.getIssueNumber()));
    }

    private String encodeDvd(Dvd dvd) {
        return String.join(FIELD_SEPARATOR,
                TYPE_DVD,
                Base64Codec.encode(dvd.getId()),
                Base64Codec.encode(dvd.getTitle()),
                String.valueOf(dvd.getDurationMinutes()));
    }

    private String encodeGroup(LibraryItemGroup group) {
        String encodedChildren = group.getChildren().stream()
                .map(this::encode)
                .map(Base64Codec::encode)
                .collect(Collectors.joining(GROUP_CHILD_SEPARATOR));

        return String.join(FIELD_SEPARATOR,
                TYPE_GROUP,
                Base64Codec.encode(group.getId()),
                Base64Codec.encode(group.getTitle()),
                encodedChildren);
    }

    private static Book decodeBook(String[] parts) {
        requireLength(parts, 5, TYPE_BOOK);
        return Book.builder()
                .id(Base64Codec.decode(parts[1]))
                .title(Base64Codec.decode(parts[2]))
                .author(Base64Codec.decode(parts[3]))
                .isbn(Base64Codec.decode(parts[4]))
                .build();
    }

    private static Magazine decodeMagazine(String[] parts) {
        requireLength(parts, 4, TYPE_MAGAZINE);
        return Magazine.builder()
                .id(Base64Codec.decode(parts[1]))
                .title(Base64Codec.decode(parts[2]))
                .issueNumber(parsePositiveInt(parts[3], "issueNumber"))
                .build();
    }

    private static Dvd decodeDvd(String[] parts) {
        requireLength(parts, 4, TYPE_DVD);
        return Dvd.builder()
                .id(Base64Codec.decode(parts[1]))
                .title(Base64Codec.decode(parts[2]))
                .durationMinutes(parsePositiveInt(parts[3], "durationMinutes"))
                .build();
    }

    private LibraryItemGroup decodeGroup(String[] parts) {
        requireLength(parts, 4, TYPE_GROUP);

        LibraryItemGroup group = LibraryItemGroup.builder()
                .id(Base64Codec.decode(parts[1]))
                .title(Base64Codec.decode(parts[2]))
                .build();

        for (String encodedChild : decodeChildRecordTokens(parts[3])) {
            group.addChild(decode(Base64Codec.decode(encodedChild)));
        }

        return group;
    }

    private static List<String> decodeChildRecordTokens(String childrenField) {
        if (childrenField == null || childrenField.isBlank()) {
            return List.of();
        }

        return Arrays.stream(childrenField.split(GROUP_CHILD_SEPARATOR))
                .filter(token -> !token.isBlank())
                .toList();
    }

    private static int parsePositiveInt(String rawValue, String fieldName) {
        try {
            int parsed = Integer.parseInt(rawValue);
            if (parsed <= 0) {
                throw new IllegalArgumentException(fieldName + " must be > 0");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer for " + fieldName + ": " + rawValue, ex);
        }
    }

    private static void requireLength(String[] parts, int expectedLength, String type) {
        if (parts.length != expectedLength) {
            throw new IllegalArgumentException("Invalid record for " + type + ": expected " + expectedLength + " fields but got " + parts.length);
        }
    }
}
