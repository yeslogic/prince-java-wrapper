/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Encryption key sizes.
 */
public enum KeyBits {
    BITS40(40),
    BITS128(128);

    private final int keyBits;

    KeyBits(int keyBits) {
        this.keyBits = keyBits;
    }

    @Override
    public String toString() {
        return Integer.toString(this.keyBits);
    }
}
