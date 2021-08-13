/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.enums;

/**
 * Raster backgrounds.
 */
public enum RasterBackground {
    WHITE("white"),
    TRANSPARENT("transparent");

    private final String rasterBackground;

    RasterBackground(String rasterBackground) {
        this.rasterBackground = rasterBackground;
    }

    @Override
    public String toString() {
        return this.rasterBackground;
    }
}
