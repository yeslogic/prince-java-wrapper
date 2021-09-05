/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Input types.
 */
public enum InputType {
    /** Equates to the string {@code "auto"}. */
    AUTO("auto"),
    /** Equates to the string {@code "html"}. */
    HTML("html"),
    /** Equates to the string {@code "xml"}. */
    XML("xml");

    private final String inputType;

    InputType(String inputType)  {
        this.inputType = inputType;
    }

    @Override
    public String toString() {
        return this.inputType;
    }
}
