
package com.chiril.library.domain;

public final class Dvd extends LibraryItem {
    private final int durationMinutes;

    private Dvd(Builder builder) {
        super(builder.id, builder.title);
        if (builder.durationMinutes == null || builder.durationMinutes <= 0)
            throw new IllegalArgumentException("durationMinutes invalid");
        this.durationMinutes = builder.durationMinutes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public Dvd clone() {
        Builder builder = new Builder();
        builder.id(this.getId());
        builder.title(this.getTitle());
        builder.durationMinutes(this.durationMinutes);
        return builder.build();
    }

    @Override
    public String getType() {
        return "DVD";
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") +
                ", durationMinutes=" + durationMinutes + "}";
    }

    public static final class Builder {
        private String id;
        private String title;
        private Integer durationMinutes;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Dvd build() {
            return new Dvd(this);
        }
    }
}