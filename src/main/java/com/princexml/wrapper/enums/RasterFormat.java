/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Raster formats.
 */
public enum RasterFormat {
    /** Equates to the string {@code "auto"}. */
    AUTO("auto"),
    /** Equates to the string {@code "png"}. */
    PNG("png"),
    /** Equates to the string {@code "jpeg"}. */
    JPEG("jpeg");

    private final String rasterFormat;

    RasterFormat(String rasterFormat) {
        this.rasterFormat = rasterFormat;
    }

    @Override
    public String toString() {
        return this.rasterFormat;
    }
}
