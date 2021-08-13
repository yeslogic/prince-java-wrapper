/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Input types.
 */
public enum InputType {
    AUTO("auto"),
    HTML("html"),
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
