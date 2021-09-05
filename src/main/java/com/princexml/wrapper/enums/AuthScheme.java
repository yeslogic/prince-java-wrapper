/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * HTTP authentication schemes.
 */
public enum AuthScheme {
    /** Equates to the string {@code "http"}. */
    HTTP("http"),
    /** Equates to the string {@code "https"}. */
    HTTPS("https");

    private final String authScheme;

    AuthScheme(String authScheme) {
        this.authScheme = authScheme;
    }

    @Override
    public String toString() {
        return this.authScheme;
    }
}
