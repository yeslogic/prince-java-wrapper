/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Minimum-allowed SSL version.
 */
public enum SslVersion {
    /** Equates to the string {@code "default"}. */
    DEFAULT("default"),
    /** Equates to the string {@code "tlsv1"}. */
    TLSV1("tlsv1"),
    /** Equates to the string {@code "tlsv1.0"}. */
    TLSV1_0("tlsv1.0"),
    /** Equates to the string {@code "tlsv1.1"}. */
    TLSV1_1("tlsv1.1"),
    /** Equates to the string {@code "tlsv1.2"}. */
    TLSV1_2("tlsv1.2"),
    /** Equates to the string {@code "tlsv1.3"}. */
    TLSV1_3("tlsv1.3");

    private final String sslVersion;

    SslVersion(String sslVersion) {
        this.sslVersion = sslVersion;
    }

    @Override
    public String toString() {
        return this.sslVersion;
    }
}
