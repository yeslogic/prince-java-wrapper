/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Raster formats.
 */
public enum RasterFormat {
    AUTO("auto"),
    PNG("png"),
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
