package com.chiril.library.adapter.codec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Base64Codec {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private Base64Codec() {
    }

    public static String encode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        try {
            return new String(DECODER.decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid encoded value: " + value, ex);
        }
    }
}
