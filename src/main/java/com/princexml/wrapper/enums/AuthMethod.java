/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * HTTP authentication methods.
 */
public enum AuthMethod {
    /** Equates to the string {@code "basic"}. */
    BASIC("basic"),
    /** Equates to the string {@code "digest"}. */
    DIGEST("digest"),
    /** Equates to the string {@code "ntlm"}. */
    NTLM("ntlm"),
    /** Equates to the string {@code "negotiate"}. */
    NEGOTIATE("negotiate");

    private final String authMethod;

    AuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    @Override
    public String toString() {
        return this.authMethod;
    }
}
