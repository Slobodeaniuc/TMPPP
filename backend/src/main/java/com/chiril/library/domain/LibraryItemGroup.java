package com.chiril.library.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LibraryItemGroup extends LibraryItem {
    private final List<LibraryItem> children = new ArrayList<>();

    private LibraryItemGroup(Builder builder) {
        super(builder.id, builder.title);
        for (LibraryItem child : builder.children) {
            addChild(child);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<LibraryItem> getChildren() {
        return List.copyOf(children);
    }

    public void addChild(LibraryItem child) {
        if (child == null) {
            throw new IllegalArgumentException("child null");
        }
        if (getId().equals(child.getId())) {
            throw new IllegalArgumentException("A group cannot contain itself");
        }
        if (containsDirectChildWithId(child.getId())) {
            throw new IllegalArgumentException("Duplicate child id in group: " + child.getId());
        }
        if (child instanceof LibraryItemGroup group && group.containsItemWithId(getId())) {
            throw new IllegalArgumentException("Cycle detected while adding child: " + child.getId());
        }
        children.add(child);
    }

    public boolean removeChildById(String childId) {
        if (childId == null || childId.isBlank()) {
            return false;
        }
        return children.removeIf(child -> childId.equals(child.getId()));
    }

    public List<LibraryItem> flattenLeafItems() {
        List<LibraryItem> result = new ArrayList<>();
        flattenInto(result);
        return result;
    }

    @Override
    public LibraryItemGroup clone() {
        LibraryItemGroup clone = builder()
                .id(getId())
                .title(getTitle())
                .build();

        for (LibraryItem child : children) {
            clone.addChild(child.clone());
        }
        return clone;
    }

    @Override
    public String getType() {
        return "GROUP";
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") +
                ", childIds=" + children.stream().map(LibraryItem::getId).toList() + "}";
    }

    private boolean containsDirectChildWithId(String childId) {
        return children.stream().anyMatch(child -> childId.equals(child.getId()));
    }

    private boolean containsItemWithId(String itemId) {
        if (getId().equals(itemId)) {
            return true;
        }

        for (LibraryItem child : children) {
            if (itemId.equals(child.getId())) {
                return true;
            }
            if (child instanceof LibraryItemGroup group && group.containsItemWithId(itemId)) {
                return true;
            }
        }
        return false;
    }

    private void flattenInto(Collection<LibraryItem> result) {
        for (LibraryItem child : children) {
            if (child instanceof LibraryItemGroup group) {
                group.flattenInto(result);
            } else {
                result.add(child);
            }
        }
    }

    public static final class Builder {
        private String id;
        private String title;
        private final List<LibraryItem> children = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder child(LibraryItem child) {
            this.children.add(child);
            return this;
        }

        public Builder children(List<LibraryItem> children) {
            if (children == null) {
                throw new IllegalArgumentException("children null");
            }
            this.children.addAll(children);
            return this;
        }

        public LibraryItemGroup build() {
            return new LibraryItemGroup(this);
        }
    }
}
