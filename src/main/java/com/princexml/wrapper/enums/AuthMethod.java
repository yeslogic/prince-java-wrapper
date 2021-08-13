/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * HTTP authentication methods.
 */
public enum AuthMethod {
    BASIC("basic"),
    DIGEST("digest"),
    NTLM("ntlm"),
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
