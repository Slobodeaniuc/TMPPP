package com.chiril.library.domain;

public final class Magazine extends LibraryItem {
    private final int issueNumber;

    private Magazine(Builder builder) {
        super(builder.id, builder.title);
        if (builder.issueNumber == null || builder.issueNumber <= 0)
            throw new IllegalArgumentException("issueNumber invalid");
        this.issueNumber = builder.issueNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    public Magazine clone() {
        Builder builder = new Builder();
        builder.id(this.getId());
        builder.title(this.getTitle());
        builder.issueNumber(this.issueNumber);
        return builder.build();
    }

    @Override
    public String getType() {
        return "MAGAZINE";
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") +
                ", issueNumber=" + issueNumber + "}";
    }

    public static final class Builder {
        private String id;
        private String title;
        private Integer issueNumber;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder issueNumber(int issueNumber) {
            this.issueNumber = issueNumber;
            return this;
        }

        public Magazine build() {
            return new Magazine(this);
        }
    }
}