/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * PDF profiles, see <a href="https://www.princexml.com/doc/prince-output/#pdf-versions-and-profiles">PDF Versions and Profiles</a>.
 */
public enum PdfProfile {
    /** Equates to the string {@code "PDF/A-1a"}. */
    PDFA_1A("PDF/A-1a"),
    /** Equates to the string {@code "PDF/A-1a+PDF/UA-1"}. */
    PDFA_1A_AND_PDFUA_1("PDF/A-1a+PDF/UA-1"),
    /** Equates to the string {@code "PDF/A-1b"}. */
    PDFA_1B("PDF/A-1b"),
    /** Equates to the string {@code "PDF/A-2a"}. */
    PDFA_2A("PDF/A-2a"),
    /** Equates to the string {@code "PDF/A-2a+PDF/UA-1"}. */
    PDFA_2A_AND_PDFUA_1("PDF/A-2a+PDF/UA-1"),
    /** Equates to the string {@code "PDF/A-2b"}. */
    PDFA_2B("PDF/A-2b"),
    /** Equates to the string {@code "PDF/A-3a"}. */
    PDFA_3A("PDF/A-3a"),
    /** Equates to the string {@code "PDF/A-3a+PDF/UA-1"}. */
    PDFA_3A_AND_PDFUA_1("PDF/A-3a+PDF/UA-1"),
    /** Equates to the string {@code "PDF/A-3b"}. */
    PDFA_3B("PDF/A-3b"),
    /** Equates to the string {@code "PDF/UA-1"}. */
    PDFUA_1("PDF/UA-1"),
    /** Equates to the string {@code "PDF/X-1a:2001"}. */
    PDFX_1A_2001("PDF/X-1a:2001"),
    /** Equates to the string {@code "PDF/X-1a:2003"}. */
    PDFX_1A_2003("PDF/X-1a:2003"),
    /** Equates to the string {@code "PDF/X-3:2002"}. */
    PDFX_3_2002("PDF/X-3:2002"),
    /** Equates to the string {@code "PDF/X-3:2003"}. */
    PDFX_3_2003("PDF/X-3:2003"),
    /** Equates to the string {@code "PDF/X-4"}. */
    PDFX_4("PDF/X-4");

    private final String pdfProfile;

    PdfProfile(String pdfProfile) {
        this.pdfProfile = pdfProfile;
    }

    @Override
    public String toString() {
        return this.pdfProfile;
    }
}
