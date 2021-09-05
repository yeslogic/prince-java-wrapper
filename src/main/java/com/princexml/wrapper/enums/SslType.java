/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * SSL file type.
 */
public enum SslType {
    /** Equates to the string {@code "PEM"}. */
    PEM("PEM"),
    /** Equates to the string {@code "DER"}. */
    DER("DER");

    private final String sslType;

    SslType(String sslType) {
        this.sslType = sslType;
    }

    @Override
    public String toString() {
        return this.sslType;
    }
}
