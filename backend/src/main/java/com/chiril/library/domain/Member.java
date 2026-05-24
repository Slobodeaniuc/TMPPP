package com.chiril.library.domain;

public final class Member {
    private final String id;
    private final String name;

    private Member(Builder builder) {
        if (builder.id == null || builder.id.isBlank())
            throw new IllegalArgumentException("id invalid");
        if (builder.name == null || builder.name.isBlank())
            throw new IllegalArgumentException("name invalid");
        this.id = builder.id;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Member{id='" + id + "', name='" + getName() + "'}";
    }

    public static final class Builder {
        private String id;
        private String name;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Member build() {
            return new Member(this);
        }
    }
}