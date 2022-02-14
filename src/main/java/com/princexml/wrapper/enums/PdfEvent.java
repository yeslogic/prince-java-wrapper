/*
 * Copyright (C) 2022 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * PDF events.
 */
public enum PdfEvent {
    /** Equates to the string {@code "will-close"}. */
    WILL_CLOSE("will-close"),
    /** Equates to the string {@code "will-save"}. */
    WILL_SAVE("will-save"),
    /** Equates to the string {@code "did-save"}. */
    DID_SAVE("did-save"),
    /** Equates to the string {@code "will-print"}. */
    WILL_PRINT("will-print"),
    /** Equates to the string {@code "did-print"}. */
    DID_PRINT("did-print");

    private final String pdfEvent;

    PdfEvent(String pdfEvent) {
        this.pdfEvent = pdfEvent;
    }

    @Override
    public String toString() {
        return this.pdfEvent;
    }
}
