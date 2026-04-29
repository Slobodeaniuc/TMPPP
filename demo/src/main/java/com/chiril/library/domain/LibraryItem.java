package com.chiril.library.domain;

public abstract class LibraryItem implements Prototype<LibraryItem>, Cloneable {

    private final String id;
    private final String title;

    protected LibraryItem(String id, String title) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("id invalid");
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("title invalid");
        this.id = id;
        this.title = title;
    }

    @Override
    public LibraryItem clone() {
        try {
            return (LibraryItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public final String getId() {
        return id;
    }

    public final String getTitle() {
        return title;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return getType() + "{id='" + id + "', title='" + title + "'}";
    }
}